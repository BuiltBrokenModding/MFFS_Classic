var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function initializeCoreMod() {
    return {
        'setBlockEventHook': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.Level',
                'methodName': 'm_6933_',
                'methodDesc': '(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z'
            },
            'transformer': function (node) {
                var previous = null;
                for (var i = 0; i < node.instructions.size(); i++) {
                    var insn = node.instructions.get(i);
                    if (insn.getOpcode() == Opcodes.IRETURN && previous != null && previous.getOpcode() == Opcodes.ICONST_1) {
                        var list = new InsnList();
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 'dev/su5ed/mffs/util/ModUtil', 'onSetBlock', '(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V', false))
                        node.instructions.insertBefore(previous, list);
                        ASMAPI.log('DEBUG', 'Injected setBlock hook');
                        break;
                    }
                    previous = insn;
                }
                return node;
            }
        }
    }
}