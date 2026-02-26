package dev.su5ed.mffs.render.model;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class ProjectorRotorModel extends Model.Simple {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(MFFSMod.location("projector_rotor"), "main");

    public ProjectorRotorModel(ModelPart root) {
        super(root.getChild("root"), RenderTypes::entityTranslucent);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        root.addOrReplaceChild("axle",
            CubeListBuilder.create()
                .texOffs(16, 26)
                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("thingfront",
            CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(-2.0F, -2.0F, -7.0F, 4.0F, 8.0F, 4.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("thingback",
            CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(-2.0F, -2.0F, 3.0F, 4.0F, 8.0F, 4.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("thingright",
            CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("thingleft",
            CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(-6.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("attacherbig1",
            CubeListBuilder.create()
                .texOffs(16, 20)
                .addBox(-7.0F, -1.0F, -3.0F, 14.0F, 1.0F, 6.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("attacherbig2",
            CubeListBuilder.create()
                .texOffs(16, 20)
                .addBox(-7.0F, 4.0F, -3.0F, 14.0F, 1.0F, 6.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("attachersmall3",
            CubeListBuilder.create()
                .texOffs(16, 36)
                .addBox(-3.0F, -1.0F, -8.0F, 6.0F, 1.0F, 5.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("attachersmall4",
            CubeListBuilder.create()
                .texOffs(16, 36)
                .addBox(-3.0F, 4.0F, -8.0F, 6.0F, 1.0F, 5.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("attachersmall2",
            CubeListBuilder.create()
                .texOffs(16, 36)
                .addBox(-3.0F, 4.0F, 3.0F, 6.0F, 1.0F, 5.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("attachersmall1",
            CubeListBuilder.create()
                .texOffs(16, 36)
                .addBox(-3.0F, -1.0F, 3.0F, 6.0F, 1.0F, 5.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }
}