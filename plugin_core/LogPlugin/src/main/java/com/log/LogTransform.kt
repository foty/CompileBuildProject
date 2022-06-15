package com.log

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Create by lxx
 * Date : 2022/6/14 15:20
 * Use by
 */
class LogTransform(private val mProject: Project) : Transform() {

    override fun getName(): String {
        return "LogTransFormKt"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(trans: TransformInvocation?) {
        super.transform(trans)
        println("[transform] --------- start ----------")

        // 获取输出目录
        val out = trans?.outputProvider
        out?.deleteAll()
        trans?.inputs?.forEach { input ->
            // 获取到所有的目录文件类输入流
            input.directoryInputs.forEach { dirInput ->
                val path = dirInput.file.absolutePath
                // 注入代码(修改代码)
                println("[transform] output_directory: $path")
                InjectByJavassistKt.inject(path, mProject)

                val outDir = trans.outputProvider.getContentLocation(
                    dirInput.name,
                    dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                )
                outDir.delete()
                // 复制到输出目录
                FileUtils.copyDirectory(dirInput.file, outDir)
            }

            // jar文件直接复制到输出目录即可(注意这步不能没有修改就不写，否则运行会报错找不到类)
            // 所有的jar类型输入流
            input.jarInputs.forEach { jarInput ->
                val dest = trans.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )

                FileUtils.copyFile(jarInput.file, dest)
                println("[transform] output_jar: $dest" + jarInput.file.absolutePath)
            }
        }

        println("[transform] --------- end -----------")
    }
}