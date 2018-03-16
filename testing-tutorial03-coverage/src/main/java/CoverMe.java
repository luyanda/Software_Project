import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CoverMe {


    public static void main(String[] args) throws IOException {
        /* Get the class file */
        FileInputStream classInputStream = new FileInputStream(new File("./exampleSrc/test.class"));

        ClassReader classReader = new ClassReader(classInputStream);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        /* Create our visitor */
        ClassVisitor classVisitor = new ClassAdapter(classWriter);

        /* Feed the class file from our reader into our writer */
        classReader.accept(classVisitor, 0);

        /* Write our new class */
        FileOutputStream fileOutputStream = new FileOutputStream("./exampleSrc/test.class");
        fileOutputStream.write(classWriter.toByteArray());
        fileOutputStream.close();
    }
}

class ClassAdapter extends ClassVisitor implements Opcodes {
    public ClassAdapter(final ClassVisitor cv) {
        super(ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
//        return mv == null ? null : new MethodAdapter(mv);
        return mv == null ? null : new MyMethodAdapter(access, name, descriptor, signature, exceptions, mv);
    }

}

class MethodAdapter extends MethodVisitor implements Opcodes {
    public MethodAdapter(final MethodVisitor mv) {
        super(ASM6, mv);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        /* TODO: System.err.println("CALL" + name); */
        VisitUtils.insertPrint(mv, "CALL " + name);

        /* Do the call */
        mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

        /* TODO: System.err.println("RETURN" + name); */
        VisitUtils.insertPrint(mv, "RETURN " + name);
    }
}

class VisitUtils implements Opcodes {
    public static void insertPrint(MethodVisitor mv, String message) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(message);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}

