package io.github.codeutilities.gui.menus.codeutils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.gui.widgets.CImage;
import io.github.codeutilities.util.Contributor;
import io.github.codeutilities.util.ILoader;
import io.github.codeutilities.util.IMenu;
import io.github.codeutilities.util.networking.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContributorsMenu extends LightweightGuiDescription implements IMenu, ILoader {
    private static ContributorsMenu INSTANCE;
    private final List<Contributor> contributors = new ArrayList<>();

    public static ContributorsMenu getInstance() {
        return INSTANCE;
    }

    @Override
    public void open(String... args) {

        INSTANCE = this;

        WPlainPanel root = new WPlainPanel();
        root.setSize(230, 220);

        WPlainPanel panel = new WPlainPanel();
        root.add(new WLabel("Contributors"), 0, 0);
        root.add(new WScrollPanel(panel), 0, 10, 230, 210);

        int y = 0;
        int x = 0;

        //todo: clean up this code (possibly remake it)
        for(Contributor contributor:contributors) {
            if(contributor.getAvatar() == null) {
                try {
                    URL url = new URL(contributor.getAvatarUrl());
                    Identifier identifier = CodeUtilities.MC.getTextureManager().registerDynamicTexture("contributor_" + contributor.getName().toLowerCase(), new NativeImageBackedTexture(NativeImage.read(url.openStream())));
                    contributor.setAvatar(identifier);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            CImage image = new CImage(contributor.getAvatar());
            image.setSize(32, 32);
            panel.add(image, x, y);
            panel.add(new WLabel(contributor.getName()), x + 35, y + 12);


            if(x == 110) {
                x = 0;
                y+=35;
            }else {
                x = 110;
            }

        }

        panel.setHost(this);
        setRootPanel(root);

    }

    @Override
    public void load() {
        INSTANCE = this;
        try {
            JsonArray array = WebUtil.getJson("https://api.github.com/repos/CodeUtilities/CodeUtilities/contributors").getAsJsonArray();
            for(JsonElement element:array) {
                JsonObject object = element.getAsJsonObject();
                this.contributors.add(new Contributor(object.get("login").getAsString(), object.get("id").getAsInt(), object.get("contributions").getAsInt(), object.get("avatar_url").getAsString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
