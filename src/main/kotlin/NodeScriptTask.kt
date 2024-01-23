import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

abstract class NodeScriptTask : DefaultTask() {
    @Internal
    abstract fun getNodeService(): Property<NodeService>

    @get:Input
    abstract val ignoreExitValue: Property<Boolean>

    @get:Input
    abstract val command: Property<String>

    @get:Input
    abstract val args: Property<String>

    @get:Input
    abstract val packageManager: Property<PackageManager>

    init {
        description = "Run node script of the name of the task"
    }

    @TaskAction
    fun run() {
        val nodeService = getNodeService().get()
        val process = nodeService.executeCommand(this, packageManager.get(), command.get(), args.get())
        process.waitFor()
        if (process.exitValue() != 0) {
            if (!ignoreExitValue.get()) {
                throw RuntimeException("Script '$command' failed with exit value ${process.exitValue()}")
            } else {
                logger.warn("Script '$command' failed with exit value ${process.exitValue()}")
            }
        } else {
            logger.info("Script '$command' completed successfully")
        }
    }
}