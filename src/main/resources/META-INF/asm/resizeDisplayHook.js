var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

// TODO Move ugly js coremods to mixins
function initializeCoreMod() {
    return {
        'resizeDisplayHook': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': 'm_5741_',
                'methodDesc': '()V'
            },
            'transformer': function (node) {
                var returnInsn = ASMAPI.findFirstInstruction(node, Opcodes.RETURN);
                node.instructions.insertBefore(returnInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, 'dev/su5ed/mffs/render/RenderPostProcessor', 'resizeDisplay', '()V', false));
                ASMAPI.log('DEBUG', 'Injected resizeDisplay hook');
                return node;
            }
        }
    }
}