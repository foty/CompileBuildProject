import org.gradle.api.Plugin
import org.gradle.api.Project

class LogsPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        println('-----------------------------')
        println('------- log log log  -------')
        println("-----------------------------")
    }
}