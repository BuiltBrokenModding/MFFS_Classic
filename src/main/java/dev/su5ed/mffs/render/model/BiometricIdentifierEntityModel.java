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

public class BiometricIdentifierEntityModel extends Model {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(location("biometric_identifier"), "main");
    
	private final ModelPart root;

    public BiometricIdentifierEntityModel(ModelPart root) {
		super(RenderType::entityTranslucent);

		this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        root.addOrReplaceChild("rightPillar", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(-4.0F, -13.0F, 4.0F));
		root.addOrReplaceChild("rightFrame", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -9.0F, 0.0F, 1.0F, 10.0F, 1.0F), PartPose.offsetAndRotation(-5.0F, -8.8F, -4.0F, -1.0996F, 0.0F, 0.0F));
        root.addOrReplaceChild("leftPillar", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(8.0F, -13.0F, 4.0F));
        root.addOrReplaceChild("topFrame", CubeListBuilder.create().texOffs(48, 0).addBox(-8.0F, 0.0F, 0.0F, 8.0F, 1.0F, 1.0F), PartPose.offset(4.0F, -13.0F, 4.0F));
        root.addOrReplaceChild("bottomFrame", CubeListBuilder.create().texOffs(48, 0).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 1.0F, 1.0F), PartPose.offset(6.0F, -8.8F, -5.0F));
        root.addOrReplaceChild("leftFrame", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -9.0F, 0.0F, 1.0F, 10.0F, 1.0F), PartPose.offsetAndRotation(6.0F, -8.8F, -4.0F, -1.0996F, 0.0F, 0.0F));
        root.addOrReplaceChild("base3", CubeListBuilder.create().texOffs(0, 38).addBox(-13.0F, 0.0F, 0.0F, 13.0F, 3.0F, 13.0F), PartPose.offset(6.5F, -5.0F, -6.5F));
        root.addOrReplaceChild("base2", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(8.0F, -8.0F, -8.0F));
        root.addOrReplaceChild("base1", CubeListBuilder.create().texOffs(0, 19).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(8.0F, -3.0F, -8.0F));
		root.addOrReplaceChild("screen", CubeListBuilder.create().texOffs(64, 0).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 6.0F, 10.0F), PartPose.offsetAndRotation(5.0F, -8.0F, -4.5F, 0.4887F, 0.0F, 0.0F));
		root.addOrReplaceChild("light2", CubeListBuilder.create().texOffs(4, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F), PartPose.offset(-5.5F, -13.5F, 5.5F));
		root.addOrReplaceChild("light1", CubeListBuilder.create().texOffs(4, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F), PartPose.offset(6.5F, -13.5F, 5.5F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}