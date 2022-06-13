import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

class InjectByJavassist {
    static void inject(String path, Project project) {
        try {
            File dir = new File(path)
            if (dir.isDirectory()) {
                dir.eachFileRecurse { File file ->
                    if (file.name.endsWith('Activity.class')) {
                        doInject2(project, file, path)
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private static void doInject2(Project project, File clsFile, String originPath) {
        println("[javassist] clsFile: $clsFile.absolutePath")
        println("[javassist] originPath: $originPath")

        String cls = new File(originPath).relativePath(clsFile).replace('/', '.')
        println("[javassist] cls: $cls")
        cls = cls.substring(0, cls.lastIndexOf('.class'))
        println("[javassist] after Cls: $cls")

        ClassPool pool = ClassPool.getDefault()
        // 加入当前路径
        pool.appendClassPath(originPath)
        pool.appendClassPath(project.android.bootClasspath[0].toString())

        CtClass cc = pool.get("com.example.compilebuildproject.MainActivity")
        CtMethod personFly = cc.getDeclaredMethod("onCreate")

        personFly.insertBefore("System.out.println(\"织入代码I\");")
        personFly.insertAfter("System.out.println(\"织入代码II\");")

        cc.writeFile(originPath)
        cc.detach()
    }


    private static void doInject(Project project, File clsFile, String originPath) {

        /**
         * 概括地讲，使用javassist修改字节码分下面步骤。
         *
         * 1、获取到目标文件的输出路径，提取到对应文件(路径格式化)
         * 2、获取ClassPool对象操作字节码；
         * 3、添加相关文件路径到ClassPool
         * 4、添加android相关的东西<相关jar、相关的包>
         * 5、找到修改对象(类、方法)
         * 6、修改
         * 7、写入文件
         * 8、释放资源
         */

        println("[javassist] clsFile: $clsFile.absolutePath")
        String cls = new File(originPath).relativePath(clsFile).replace('/', '.')
        println("[javassist] cls: $cls")
        cls = cls.substring(0, cls.lastIndexOf('.class'))
        println("[javassist] after Cls: $cls")
        ClassPool pool = ClassPool.getDefault()
        // 加入当前路径
        pool.appendClassPath(originPath)

        // 添加android相关的东西
        // project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        println("[javassist] android.bootClasspath: ${project.android.bootClasspath.toString()}")
        println("[javassist] android.bootClasspath: ${project.android.bootClasspath[0].toString()}")

        // 引入android.os.Bundle包(如果有必要需要导包，这里前面加入android.jar,这里可以不必导包)
        //  pool.importPackage('android.os.Bundle')
        CtClass ctClass = pool.getCtClass(cls)

        // 解冻
        if (ctClass.isFrozen()) ctClass.defrost()
        // 修改方法
        CtMethod ctMethod = ctClass.getDeclaredMethod('onCreate')
        String toastStr = 'android.widget.Toast.makeText(this, "I am the injected code", ' +
                'android.widget.Toast.LENGTH_LONG).show();'
        // 方法尾插入
        ctMethod.insertAfter(toastStr)
        // 写入文件
        ctClass.writeFile(originPath)
        // 释放
        ctClass.detach()

        println("[javassist] ---modify end----")
    }
}