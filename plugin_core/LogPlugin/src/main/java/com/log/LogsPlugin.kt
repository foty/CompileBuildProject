package com.log

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Create by lxx
 * Date : 2022/6/14 15:13
 * Use by
 */
class LogsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("-----  apply plugin start -----")
        val android = target.extensions.getByType(AppExtension::class.java)
        println(android)
        android.registerTransform(LogTransform(target))
        println("----  apply plugin end ------")
    }
}