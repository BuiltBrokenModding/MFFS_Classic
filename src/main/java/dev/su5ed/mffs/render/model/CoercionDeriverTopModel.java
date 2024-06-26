package dev.su5ed.mffs.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

import static dev.su5ed.mffs.MFFSMod.location;

public class CoercionDeriverTopModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(location("coercion_deriver_top"), "main");

    private final ModelPart root;

    public CoercionDeriverTopModel(ModelPart root) {
        super(RenderType::entityTranslucent);

        this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        root.addOrReplaceChild("tout",
            CubeListBuilder.create()
                .texOffs(24, 19)
                .addBox(-2.0F, 14.0F, -2.0F, 4.0F, 1.0F, 4.0F),
            PartPose.offset(0.0F, -24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        this.root.render(poseStack, vertexConsumer, i, i1, i2);
    }
}