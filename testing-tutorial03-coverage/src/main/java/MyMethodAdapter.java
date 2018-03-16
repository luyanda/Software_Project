import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public class MyMethodAdapter extends MethodNode implements Opcodes {
    public MyMethodAdapter(int access, String name, String desc,
                           String signature, String[] exceptions, MethodVisitor mv) {
        super(ASM6, access, name, desc, signature, exceptions);
        this.mv = mv;
    }


    /**
     * Instrument a print statement after the given node.
     *
     * @param instructions The {@link InsnList} we are inserting into
     * @param location     The {@link AbstractInsnNode} after which we want to insert our statement
     * @param message      The message to be printed out
     */
    public void instrumentPrintStatement(InsnList instructions, AbstractInsnNode location, String message) {
        FieldInsnNode fieldInsnNode = new FieldInsnNode(
                GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");

        LdcInsnNode ldcInsnNode = new LdcInsnNode("___:" + message);

        MethodInsnNode methodInsnNode = new MethodInsnNode(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false);

        InsnList insnList = new InsnList();

        insnList.add(fieldInsnNode);
        insnList.add(ldcInsnNode);
        insnList.add(methodInsnNode);

        if (location == null) {
            instructions.insert(insnList);
            return;
        }

        instructions.insert(location, insnList);

    }

    public void instrumentInstrCountArray(InsnList instructions, int size) {
        IntInsnNode arraysize = new IntInsnNode(BIPUSH, size);

        IntInsnNode booleanarray = new IntInsnNode(NEWARRAY, T_BOOLEAN);

        VarInsnNode varInsnNode = new VarInsnNode(ASTORE, 254);

        InsnList insnList = new InsnList();

        insnList.add(arraysize);
        insnList.add(booleanarray);
        insnList.add(varInsnNode);

        instructions.insert(insnList);
    }

    public void instrumentInstrCountHit(InsnList instructions, AbstractInsnNode location, int index) {
        VarInsnNode varInsnNode = new VarInsnNode(ALOAD, 254);

        IntInsnNode intInsnNode = new IntInsnNode(BIPUSH, index);

        InsnNode insnNode = new InsnNode(ICONST_1);

        InsnNode insnNode1 = new InsnNode(BASTORE);

        InsnList insnList = new InsnList();

        insnList.add(varInsnNode);
        insnList.add(intInsnNode);
        insnList.add(insnNode);
        insnList.add(insnNode1);

        instructions.insert(location, insnList);
    }


    @Override
    public void visitEnd() {
        // put your transformation code here
        System.out.println(name + ":total:" + instructions.size());
        instrumentPrintStatement(instructions, null, name + ":total:" + instructions.size());

        Iterator<AbstractInsnNode> nodeIter = instructions.iterator();

        int i = -1;

        while (nodeIter.hasNext()) {
            AbstractInsnNode node = nodeIter.next();
            if (i <= 0) {
                i++;
                continue;
            }
            instrumentPrintStatement(instructions, node, name + ":exec:" + i++);
        }

        accept(mv);
    }
}