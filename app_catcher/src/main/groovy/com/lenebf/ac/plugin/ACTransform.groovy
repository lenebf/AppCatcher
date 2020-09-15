package com.lenebf.ac.plugin

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.builder.utils.ZipEntryUtils
import com.lenebf.ac.asm.ACClassVisitor
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ACTransform extends Transform {

    @Override
    String getName() {
        return "ACTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        // 处理范围：主功能，子Module，引入的Jar或者AAR
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        assert outputProvider != null
        outputProvider.deleteAll()
        Collection<TransformInput> inputs = transformInvocation.inputs
        ACClassVisitor.clearComponentAppNames()
        inputs.forEach { TransformInput input ->
            input.directoryInputs.forEach { DirectoryInput directoryInput ->
                directoryInput.file.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File file ->
                    ClassVisitor classVisitor = new ACClassVisitor(file.getAbsolutePath())
                    ClassReader classReader = new ClassReader(file.bytes)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                }
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            input.jarInputs.forEach { JarInput jarInput ->
                FileInputStream fis = null
                ZipInputStream zis = null
                try {
                    fis = new FileInputStream(jarInput.file)
                    zis = new ZipInputStream(fis)
                    ZipEntry entry = zis.getNextEntry()
                    while (entry != null && ZipEntryUtils.isValidZipEntryName(entry)) {
                        if (!entry.isDirectory() && entry.getName().endsWith(SdkConstants.DOT_CLASS)) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int num = -1
                            while ((num = zis.read(buffer, 0, buffer.length)) > -1) {
                                byteArrayOutputStream.write(buffer, 0, num)
                            }
                            ClassVisitor classVisitor = new ACClassVisitor(null)
                            ClassReader classReader = new ClassReader(byteArrayOutputStream.toByteArray())
                            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                            byteArrayOutputStream.close()
                        }
                        entry = zis.getNextEntry()
                    }
                } finally {
                    if (zis != null) {
                        zis.close()
                    }
                    if (fis != null) {
                        fis.close()
                    }
                }
                def dest = outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}