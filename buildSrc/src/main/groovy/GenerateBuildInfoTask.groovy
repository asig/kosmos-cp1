/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.asigner.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class GenerateBuildInfoTask extends DefaultTask {

    @Internal
    def File outputDir = new File(project.buildDir.path + "/src/")

    @Internal
    def packageName = "com.asigner.cp1"

    @Internal
    def className = "BuildInfoImpl"

    @Internal
    def getGitCommit() {
        def proc = "git log -1".execute()
        def commit = proc.in.newReader().readLine().split(" ")[1]
        proc.out.close()
        proc.waitFor()
        if (proc.exitValue()) {
            println "could not get commit from git: "
            println "[ERROR] ${proc.getErrorStream()}"
        }
        assert !proc.exitValue()
        return commit;
    }

    @Internal
    def getHostName() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return System.getenv("COMPUTERNAME");
        } else {
            def proc = "hostname".execute()
            return proc.in.newReader().readLine()
        }
    }

    @Internal
    def getVersion() {
        def props = new Properties()
        props.load(new FileInputStream("build.properties"))
        return props.get("version");
    }

    @TaskAction
    def generate() {
        def now = System.currentTimeMillis();

        def targetFileName = outputDir.path + "/" + packageName.replaceAll('\\.', '/') + "/" + className + ".java"
        def targetFile = new File(targetFileName)

        def osName = System.properties.get("os.name");
        def osArch = System.properties.get("os.arch");
        def platform = System.properties.get("os.name") + " " + System.properties.get("os.version") + " " + System.properties.get("os.arch")
        targetFile.delete();
        targetFile.getParentFile().mkdirs()
        targetFile << "package ${packageName};\n"
        targetFile << "final public class ${className} implements BuildInfo {\n"
        targetFile << "public Version getVersion() { return new Version(\"${getVersion()}\"); }\n"
        targetFile << "public java.time.Instant getBuildTime() { return java.time.Instant.ofEpochMilli(${now}L); }\n"
        targetFile << "public String getCommit() { return \"${getGitCommit()}\"; }\n"
        targetFile << "public String getOSName() { return \"${osName}\"; }\n"
        targetFile << "public String getOSArch() { return \"${osArch}\"; }\n"
        targetFile << "public String toString() { return String.format(\"%s/%s/%s\", getBuildTime(), getCommit(), getOSName() + \" \" + getOSArch()); }\n"
        targetFile << '}\n'

        println "Generated build info class in " + targetFileName
    }
}
