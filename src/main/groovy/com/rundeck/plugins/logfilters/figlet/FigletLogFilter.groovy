/*
 * Copyright 2017 Rundeck, Inc. (http://rundeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rundeck.plugins.log

import com.dtolabs.rundeck.core.logging.LogEventControl
import com.dtolabs.rundeck.core.logging.LogLevel
import com.dtolabs.rundeck.core.logging.PluginLoggingContext
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription
import com.dtolabs.rundeck.plugins.descriptions.PluginMetadata
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty
import com.dtolabs.rundeck.plugins.descriptions.SelectValues
import com.dtolabs.rundeck.plugins.logging.LogFilterPlugin
import com.github.dtmo.jfiglet.FigFont
import com.github.dtmo.jfiglet.FigFontResources
import com.github.dtmo.jfiglet.FigletRenderer

// See this example code for reference:
//   https://github.com/jamesnetherton/lolcat4j/blob/master/src/test/java/com/github/jamesnetherton/lolcat4j/internal/console/ConsolePainterTest.java

@Plugin(name = FigletLogFilterPlugin.PROVIDER_NAME, service = ServiceNameConstants.LogFilter)
@PluginDescription(title = 'Figlet',
        description = 'Make large letters out of ordinary text.')
@PluginMetadata(key = 'faicon', value = 'heading')
class FigletLogFilterPlugin implements LogFilterPlugin {
    public static final String PROVIDER_NAME = 'figlet'

    @PluginProperty(
            title = 'figFont',
            description = '''The fig font name (default: standard).''',
            defaultValue = 'standard'
    )
    @SelectValues(values=["banner","big","block","bubble","digital","ivrit","lean","mini","mnemonic",
                    "script","shadow","slant","small","smscript","smshadow","smslant","standard","term"],
            freeSelect=false
    )
    String figFont


    @Override
    void handleEvent(final PluginLoggingContext context, final LogEventControl event) {
        if (event.eventType == 'log' && event.loglevel == LogLevel.NORMAL && event.message?.length() > 0) {
            try {
                event.message = filterText(event.message)
            } catch (Throwable t) {
                //event.message = t.toString()
            }
        }
    }

    String filterText(final String text) {
        FigFont figFont = FigFontResources.loadFigFontResource(figFont+".flf")
        def renderer = new FigletRenderer(figFont);
        renderer.renderText(text)
    }

}
