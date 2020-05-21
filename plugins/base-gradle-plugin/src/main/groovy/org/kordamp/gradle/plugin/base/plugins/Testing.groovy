/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2020 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle.plugin.base.plugins

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.test.tasks.FunctionalTest
import org.kordamp.gradle.plugin.test.tasks.IntegrationTest
import org.kordamp.gradle.util.ConfigureUtil

/**
 * @author Andres Almiray
 * @since 0.14.0
 */
@CompileStatic
class Testing extends AbstractFeature {
    static final String PLUGIN_ID = 'org.kordamp.gradle.testing'

    boolean logging = true
    boolean aggregate = true

    private boolean loggingSet = false
    private boolean aggregateSet = false

    final Integration integration
    final Functional functional

    private final Set<Test> testTasks = new LinkedHashSet<>()
    private final Set<IntegrationTest> integrationTasks = new LinkedHashSet<>()
    private final Set<FunctionalTest> functionalTestTasks = new LinkedHashSet<>()

    Testing(ProjectConfigurationExtension config, Project project) {
        super(config, project)
        integration = new Integration(this)
        functional = new Functional(this)
    }

    void setLogging(boolean logging) {
        this.logging = logging
        this.loggingSet = true
    }

    boolean isLoggingSet() {
        this.loggingSet
    }

    void setAggregate(boolean aggregate) {
        this.aggregate = aggregate
        this.aggregateSet = true
    }

    boolean isAggregateSet() {
        this.aggregateSet
    }

    @Override
    Map<String, Map<String, Object>> toMap() {
        new LinkedHashMap<>('testing': new LinkedHashMap<>([
            enabled    : enabled,
            logging    : logging,
            aggregate  : aggregate,
            integration: integration.toMap(),
            functional : functional.toMap()
        ]))
    }

    void integration(Action<? super Integration> action) {
        action.execute(integration)
    }

    void integration(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Integration) Closure<Void> action) {
        ConfigureUtil.configure(action, integration)
    }

    void functional(Action<? super Functional> action) {
        action.execute(functional)
    }

    void functional(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Functional) Closure<Void> action) {
        ConfigureUtil.configure(action, functional)
    }

    static void merge(Testing o1, Testing o2) {
        AbstractFeature.merge(o1, o2)
        o1.setLogging((boolean) (o1.loggingSet ? o1.logging : o2.logging))
        o1.setAggregate((boolean) (o1.aggregateSet ? o1.aggregate : o2.aggregate))
        Integration.merge(o1.integration, o2.integration)
        Functional.merge(o1.functional, o2.functional)
    }

    void postMerge() {
        integration.postMerge()
        functional.postMerge()
    }

    Set<Test> testTasks() {
        testTasks
    }

    Set<IntegrationTest> integrationTasks() {
        integrationTasks
    }

    Set<FunctionalTest> functionalTestTasks() {
        functionalTestTasks
    }
}
