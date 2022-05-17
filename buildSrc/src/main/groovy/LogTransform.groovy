import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class LogTransform extends Transform {

    private Project mProject

    LogTransform(Project mProject) {
        this.mProject = mProject
    }

    /**
     * transform的名字，也对应了该Transform所代表的Task名称.
     */
    @Override
    String getName() {
        return "LogTransForm"
    }

    /**
     * transform输入类型，可在TransformManager查看更多类型,常见的有
     * CONTENT_CLASS  class文件
     * CONTENT_JARS   jar文件
     * CONTENT_DEX   dex文件
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * transform输入文件所属范畴，可在TransformManager查看更多类型,常见的有
     * SCOPE_FULL_PROJECT  代表所有Project
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 表示是否支持增量编译
     */
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation trans) throws TransformException, InterruptedException, IOException {

        // 如果没有看到输出这行log，把"\app\build\intermediates\transforms"删除重新build
        println("[transform] ------- start --------")

        // 获取输出目录
//        def out = trans.outputProvider
//        out.deleteAll()

        mProject.android.bootClasspath.each {
            println("[mProject.android.bootClasspath] ${it.absolutePath}")
        }

        trans.inputs.each { input ->
            // 获取到所有的目录文件类输入流
            input.directoryInputs.each { dirInput ->
                String path = dirInput.file.absolutePath
                println("[transform] path= $path]")

                // 注入代码(修改代码)
                InjectByJavassist.inject(path, mProject)

                def outDir = trans.outputProvider.getContentLocation(dirInput.name,
                        dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                outDir.deleteDir()
                // 复制到输出目录
                FileUtils.copyDirectory(dirInput.file, outDir)
            }
        }

        // jar文件直接复制到输出目录即可(注意这步不能没有修改就不写，否则运行会报错找不到类)
        trans.inputs.each { input ->
            // 所有的jar类型输入流
            input.jarInputs.each { jarInput ->
                def dest = trans.outputProvider.getContentLocation(jarInput.name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                println("[transform] jar_output: $dest")
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        println("[transform] ------- end ---------")
    }
}