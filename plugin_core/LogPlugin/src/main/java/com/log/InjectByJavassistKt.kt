package com.log

import javassist.ClassPool
import org.gradle.api.Project
import java.io.File

class InjectByJavassistKt {
    companion object {

        fun inject(directoryPath: String, project: Project) {
            try {
                val dir = File(directoryPath)
                if (dir.isDirectory) {
                    dir.walkTopDown().filter { it.isFile }
                        .forEach { file ->
                            println("[javassist] file: ${file.absolutePath}")
                            if (file.name.endsWith("Activity.class"))
                                doInject2(project, file, directoryPath)
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun doInject2(project: Project, clsFile: File, originPath: String) {
            println("[javassist] ------ javassist modify start ------")
            println("[javassist] clsFile: ${clsFile.absolutePath}")
            println("[javassist] originPath: $originPath")

//            var cls = File(originPath).relativeTo(clsFile).absolutePath.replace('/', '.')
            var cls = Utils.relativePath(File(originPath), clsFile).replace('/', '.')
            println("[javassist] cls=  $cls")
            cls = cls.substring(0, cls.lastIndexOf(".class"))
            println("[javassist] after cls= $cls")

            val pool = ClassPool.getDefault()
            // 加入当前路径
            pool.appendClassPath(originPath)

            // 加入android相关环境(groovy可以这样使用)
//            pool.appendClassPath(project.android.bootClasspath[0].toString())
            // kotlin写法不允许上面的写法，未找到动态加载的方法。只能固定路径
            pool.insertClassPath("F:\\androidStudio\\sdk\\platforms\\android-30\\android.jar")

            pool.importPackage("android.widget.Toast")
            // 引入android.os.Bundle包，因为onCreate方法参数有Bundle
            pool.importPackage("android.os.Bundle")

            val cc = pool.get("com.example.compilebuildproject.MainActivity")
            //解冻
            if (cc.isFrozen) cc.defrost()

            // 修改 onCreate()
            val personFly = cc.getDeclaredMethod("onCreate")
            println("[javassist] $personFly")
            personFly.insertBefore("System.out.println(\"织入代码I\");")
            personFly.insertAfter("System.out.println(\"织入代码II\");")

            //修改say()方法
            val say = cc.getDeclaredMethod("say")
            say.insertAfter("android.widget.Toast.makeText(this,\"我是字节码修改添加的语句\",Toast.LENGTH_LONG).show();")
            say.insertAfter("System.out.println(\"say()：我是javassist织入的代码\");")

            // 打印MainActivity的所有方法
            val methods = cc.declaredMethods
            methods.forEach {
                println("[javassist] ${it.name}")
                if (it.name.contains("say")) {
                }
            }

            cc.writeFile(originPath)
            cc.detach()
            println("------ javassist modify end ------")
        }


        private fun doInject(project: Project, clsFile: File, originPath: String) {

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
//            var cls = File(originPath).relativeTo(clsFile).absolutePath.replace('/', '.')
            var cls = Utils.relativePath(File(originPath), clsFile).replace('/', '.')
            println("[javassist] cls: $cls")
            cls = cls.substring(0, cls.lastIndexOf(".class"))
            println("[javassist] after Cls: $cls")
            val pool = ClassPool.getDefault()
            // 加入当前路径
            pool.appendClassPath(originPath)

            // 添加android相关的东西
            // project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
//        pool.appendClassPath(project.android.bootClasspath[0].toString())
//        println("[javassist] android.bootClasspath: ${project.android.bootClasspath.toString()}")
//        println("[javassist] android.bootClasspath: ${project.android.bootClasspath[0].toString()}")

            // 引入android.os.Bundle包(如果有必要需要导包，这里前面加入android.jar,这里可以不必导包)
            //  pool.importPackage('android.os.Bundle')
            val ctClass = pool.getCtClass(cls)

            // 解冻
            if (ctClass.isFrozen()) ctClass.defrost()
            // 修改方法
            val ctMethod = ctClass.getDeclaredMethod("onCreate")
            val toastStr = """android.widget.Toast.makeText(this, "I am the injected code", """ +
                    """android.widget.Toast.LENGTH_LONG).show();"""
            // 方法尾插入
            ctMethod.insertAfter(toastStr)
            // 写入文件
            ctClass.writeFile(originPath)
            // 释放
            ctClass.detach()

            println("[javassist] ---modify end----")
        }

    }
}