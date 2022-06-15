import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LogsPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        println('-----  load plugin   -----')
        //要想使用自定义的transform，就要先注册transform。

        //获取build.gradle中的android闭包
        AppExtension android = target.getExtensions().findByType(AppExtension.class)
        //注册transform
        android.registerTransform(new LogTransform(target))

//       android.applicationVariants.all {
//            def app = it as ApplicationVariantImpl
//            println("variant name: ${app.name}")
//        }

        //为Project创建一个Extension
//        target.extensions.create("student", StudentBean.class)

        println("----  load plugin end ------")
    }
}