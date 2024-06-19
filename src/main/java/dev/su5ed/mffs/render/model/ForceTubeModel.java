package dev.su5ed.mffs.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.su5ed.mffs.render.ModRenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static dev.su5ed.mffs.MFFSMod.location;

public class ForceTubeModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(location("force_tube"), "main");

    private static final ResourceLocation CORE_TEXTURE = location("textures/model/force_cube.png");
    public static final RenderType RENDER_TYPE = ModRenderType.STANDARD_TRANSLUCENT_ENTITY.apply(CORE_TEXTURE);

    private final ModelPart root;

    public ForceTubeModel(ModelPart root) {
        super(ModRenderType.STANDARD_TRANSLUCENT_ENTITY);

        this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("root",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 1.0F, 16.0F, 16.0F)
                .texOffs(0, 0).addBox(7.0F, -8.0F, -8.0F, 1.0F, 16.0F, 16.0F)
                .texOffs(0, 0).addBox(-7.0F, -8.0F, -8.0F, 14.0F, 1.0F, 16.0F)
                .texOffs(0, 0).addBox(-7.0F, 7.0F, -8.0F, 14.0F, 1.0F, 16.0F),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        this.root.render(poseStack, vertexConsumer, i, i1, i2);
    }
}