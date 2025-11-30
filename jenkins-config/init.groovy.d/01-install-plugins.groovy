import jenkins.model.*
import java.util.logging.Logger

def logger = Logger.getLogger("")

def plugins = [
    'pipeline-stage-view',
    'workflow-aggregator',
    'gradle',
    'htmlpublisher',
    'configuration-as-code'
]

def instance = Jenkins.getInstance()
def pm = instance.getPluginManager()
def uc = instance.getUpdateCenter()

logger.info("Installing plugins: ${plugins}")

plugins.each { pluginName ->
    if (!pm.getPlugin(pluginName)) {
        logger.info("Installing plugin: ${pluginName}")
        def plugin = uc.getPlugin(pluginName)
        if (plugin) {
            plugin.deploy()
        }
    } else {
        logger.info("Plugin already installed: ${pluginName}")
    }
}

instance.save()
