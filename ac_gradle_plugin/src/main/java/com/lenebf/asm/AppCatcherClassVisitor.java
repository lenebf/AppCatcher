package com.lenebf.asm;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lenebf@126.com
 * @since 2020/9/19
 */
public class AppCatcherClassVisitor extends ClassVisitor {
    private static final String COMPONENT_APP_NAME = "com/lenebf/ac/component_application/ComponentApplication";
    private static final String MAIN_APP_NAME = "com/lenebf/ac/main_application/MainApplication";
    private static final List<String> componentAppNames = new ArrayList<>();

    private static String catcherClassName = null;
    private static String catcherClassPath = null;

    private String classPath;

    public AppCatcherClassVisitor(String classPath) {
        super(Opcodes.ASM7, null);
        this.classPath = classPath;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if (COMPONENT_APP_NAME.equals(superName)) {
            componentAppNames.add(name);
        } else if (MAIN_APP_NAME.equals(superName)) {
            catcherClassName = name;
            catcherClassPath = classPath;
            buildCatchedApps();
        }
    }

    public static void clearComponentAppNames() {
        componentAppNames.clear();
        catcherClassPath = null;
    }

    private static void buildCatchedApps() {
        if (componentAppNames.isEmpty() || catcherClassName == null || catcherClassPath == null) {
            return;
        }
        System.out.println("ACClassVisitor ------> " + catcherClassPath);
        int nameIndex = catcherClassName.lastIndexOf("/");
        String className = catcherClassName.substring(0, nameIndex + 1) + "CatchedApps";

        ClassWriter cw = new ClassWriter(0);
        // 创建新的类
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, null, "java/lang/Object", null);
        cw.visitSource("CatchedApps.java", null);
        // 声明 componentApps 变量
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC, "componentApps", "Ljava/util/List;", "Ljava/util/List<Ljava" +
                "/lang/String;>;", null);
        fv.visitEnd();
        // 创建构造方法
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitFieldInsn(Opcodes.PUTFIELD, className, "componentApps", "Ljava/util/List;");
        // 将SubApp写入List
        for (String name : componentAppNames) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, className, "componentApps", "Ljava/util/List;");
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(Opcodes.POP);
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(3, 1);
        mv.visitEnd();
        cw.visitEnd();
        // 将内容写入文件
        byte[] bytes = cw.toByteArray();

        nameIndex = catcherClassPath.lastIndexOf(File.separator);
        String classPath = catcherClassPath.substring(0, nameIndex + 1) + "CatchedApps.class";

        FileOutputStream outputStream = null;
        try {
            File classFile = new File(classPath);
            classFile.createNewFile();
            outputStream = new FileOutputStream(classFile);
            outputStream.write(bytes);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

    }
}
