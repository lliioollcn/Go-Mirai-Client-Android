package cn.lliiooll.dhl.plugin

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.http.HttpUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream
import java.lang.RuntimeException
import java.nio.charset.StandardCharsets

class PGPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val dir = File(project.projectDir, "temp")
        val goDir = File(dir, "go")
        val goBinDir = File(goDir, "bin")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        if (!goBinDir.exists() || !"${goBinDir.absolutePath}\\go.exe".exec(goBinDir)) {
            println("go未安装，正在自动安装...")
            val file = File(dir, "go.zip")
            if (!file.exists()) {
                file.createNewFile()
                doDownload("https://dl.google.com/go/go1.20.2.windows-amd64.zip", file)
            } else {
                if (SecureUtil.sha256(file) != "fe439f0e438f7555a7f5f7194ddb6f4a07b0de1fa414385d19f2aeb26d9f43db") {
                    doDownload("https://dl.google.com/go/go1.20.2.windows-amd64.zip", file)
                }
            }
            if (!goDir.exists()) {
                "tar -xzvf go.zip go".exec(dir)
            }
        }

        System.out.println("Gomobile不存在，自动安装...")
        val srcDir = File(dir, "mobile-master")
        if (srcDir.exists()) {
            srcDir.delete()
        }
        val srcFile = File(dir, "sources.zip")
        if (!srcFile.exists()) {
            doDownload(
                "https://github.com/golang/mobile/archive/refs/heads/master.zip", srcFile
            )
            "tar -xzvf sources.zip".exec(dir)
        }
        val buildScript = File(srcDir, "build.bat")
        val adminScript = File(srcDir, "admin.bat")
        val goFile = File(goBinDir, "go.exe")
        val staticFile = File(dir, "static.zip")
        if (!staticFile.exists()) {
            doDownload(
                "https://github.com/ProtobufBot/pbbot-react-ui/releases/latest/download/static.zip",
                staticFile
            )
        }
        "tar -xzvf static.zip".exec(File(goDir, "pkg\\static\\"))
        FileUtil.writeString(
            "@echo off\n" + "set GOMODCACHE=${goBinDir.absolutePath}\\pkg\\mod\n" + "set GOPATH=${goBinDir.absolutePath}\n" + "${goFile.absolutePath} env -w GO111MODULE=on \n" + "${goFile.absolutePath} env -w GOPROXY=https://goproxy.cn\n" + "${goFile.absolutePath} build golang.org/x/mobile/cmd/gomobile\n" + "exit",
            buildScript,
            StandardCharsets.UTF_8
        )
        FileUtil.writeString(
            "@echo off\n" + "%1 mshta vbscript:CreateObject(\"Shell.Application\").ShellExecute(\"cmd.exe\",\"/c %~s0 ::\",\"\",\"runas\",1)(window.close)&&exit\n" + "cd /d \"%~dp0\"\n" + "start build.bat\n" + "exit",
            adminScript,
            StandardCharsets.UTF_8
        )

        val gomobileFile = File(srcDir, "gomobile.exe")
        if (gomobileFile.exists()) gomobileFile.delete()
        adminScript.absolutePath.exec1(srcDir)
        var startTime = System.currentTimeMillis()
        while (!gomobileFile.exists()) {
            Thread.sleep(1000L)
            if (System.currentTimeMillis() - startTime > 1000 * 60 * 10) {
                throw RuntimeException("GoMobile编译失败，请手动启动 ${adminScript.absolutePath} 进行编译")
            }
        }
        gomobileFile.absolutePath.exec()
        println("安装成功，开始编译库...")
        val gmcDir = File(project.projectDir, "../gmc")
        val buildGMCScript = File(gmcDir, "buildGMC.bat")
        val adminGMCScript = File(gmcDir, "adminGMC.bat")
        FileUtil.writeString(
            "@echo off\n" + "%1 mshta vbscript:CreateObject(\"Shell.Application\").ShellExecute(\"cmd.exe\",\"/c %~s0 ::\",\"\",\"runas\",1)(window.close)&&exit\n" + "cd /d \"%~dp0\"\n" + "start buildGMC.bat\n" + "exit",
            adminGMCScript,
            StandardCharsets.UTF_8
        )
        FileUtil.writeString(
            "@echo off\n" +
                    "set GOMODCACHE=${goDir.absolutePath}\\pkg\\mod\n" +
                    "set GOPATH=${goDir.absolutePath}\n" +
                    "set PATH=%PATH%;${goBinDir.absolutePath}\n" +
                    "set ANDROID_HOME=D:\\env\\androidSdk\n" +
                    "${gomobileFile.absolutePath} init\n" +
                    "${gomobileFile.absolutePath} bind -target=android ./service/gmc_android\n" +
                    "exit",
            buildGMCScript,
            StandardCharsets.UTF_8
        )
        val replaceText = File(gmcDir, "service\\gmc_android\\gmc.go")
        FileUtil.writeLines(
            FileUtil.readLines(replaceText, StandardCharsets.UTF_8).stream().filter {
                return@filter !it.startsWith("//")
            }.toList(), replaceText, StandardCharsets.UTF_8
        )
        adminGMCScript.absolutePath.exec1(gmcDir)
        val aarFile = File(goFile, "gmc_android.aar")
        if (aarFile.exists()) aarFile.delete()
        startTime = System.currentTimeMillis()
        while (!aarFile.exists()) {
            Thread.sleep(1000L)
            if (System.currentTimeMillis() - startTime > 1000 * 60 * 10) {
                throw RuntimeException("GoMobile编译失败，请手动启动 ${adminGMCScript.absolutePath} 进行编译")
            }
        }
        println("编译成功，正在初始化...")
        FileUtil.copyFile(aarFile, File(project.projectDir, "libs\\" + aarFile.name))
        println("初始化完毕")


    }

    private fun doDownload(url: String, file: File) {
        try {
            IoUtil.write(
                FileOutputStream(file), true, IoUtil.readBytes(
                    HttpUtil.createGet(url).setFollowRedirects(true).execute().bodyStream(), true
                )
            )
        } catch (e: Throwable) {
            throw RuntimeException("Go自动下载失败，请安装Go/GoMobile后重试", e)
        }
    }

}

