package org.vaadin.artur.simplechat;

import org.vaadin.artur.simplechat.data.Chatter;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ChatterIndicator extends VerticalLayout {

    private Chatter chatter;

    public ChatterIndicator(Chatter c) {
        setSizeUndefined();
        this.chatter = c;

        Image img = new Image();
        img.setWidth("50px");
        img.setSource(new ExternalResource(c.imageUrl));
        setDescription(c.name);
        img.setStyleName(c.styleName);

        Label nameLabel = new Label(c.name);
        nameLabel.setSizeUndefined();
        addComponents(img, nameLabel);
        setComponentAlignment(img, Alignment.TOP_CENTER);
        setComponentAlignment(nameLabel, Alignment.TOP_CENTER);
    }

    public boolean isChatter(Chatter chatter) {
        return chatter == this.chatter;
    }

}
