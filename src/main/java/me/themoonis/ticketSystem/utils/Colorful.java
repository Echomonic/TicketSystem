package me.themoonis.ticketSystem.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class Colorful {

    private final MiniMessage message = MiniMessage
            .builder()
            .tags(TagResolver.builder()
                    .tag("link", Colorful::linkTag)
                    .tag("effect", Colorful::effectsTag)
                    .resolver(TagResolver.standard())
                    .build()
            )
            .build();

    public Component text(String text) {
        return MiniMessage.miniMessage().deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public List<Component> text(List<String> strings) {
        return strings.stream().map(Colorful::text).collect(Collectors.toList());
    }

    public Component getProgressBar(int totalBars, int maxPercent, int current, String symbol, String secondSymbol) {
        return text(getProgressBarString(totalBars, maxPercent, current, symbol, secondSymbol));
    }

    public Component getProgressBar(int totalBars, int maxPercent, double current, String filledSymbol, String halfSymbol, String emptySymbol) {
        return text(getProgressBarString(totalBars, maxPercent, current, filledSymbol, halfSymbol, emptySymbol));
    }

    public String getProgressBarString(int totalBars, int maxPercent, int current, String symbol, String secondSymbol) {
        float percent = (float) current / maxPercent;
        int progressBars = (int) (totalBars * percent);
        symbol = MiniMessage.miniMessage().serialize(text(symbol));
        secondSymbol = MiniMessage.miniMessage().serialize(text(secondSymbol));

        return symbol.repeat(progressBars) + secondSymbol.repeat(totalBars - progressBars);
    }

    public String getProgressBarString(int totalBars, int maxPercent, double current, String filledSymbol, String halfSymbol, String emptySymbol) {
        StringBuilder style = new StringBuilder();
        totalBars -= 1;
        double heart = (double) maxPercent / totalBars;
        double halfHeart = heart / 2;
        double tempHealth = current;
        int left = totalBars;

        filledSymbol = MiniMessage.miniMessage().serialize(text(filledSymbol));
        halfSymbol = MiniMessage.miniMessage().serialize(text(halfSymbol));
        emptySymbol = MiniMessage.miniMessage().serialize(text(emptySymbol));

        if (maxPercent != current && current >= 0) {
            for (int i = 0; i < maxPercent; i++) {
                if (tempHealth - heart > 0) {
                    tempHealth = tempHealth - heart;

                    style.append(filledSymbol);
                    left--;
                } else {
                    break;
                }
            }

            if (tempHealth > halfHeart) {
                style.append(filledSymbol);
                left--;
            } else if (tempHealth > 0 && tempHealth <= halfHeart) {
                style.append(halfSymbol);
                left--;
            }
        }

        if (maxPercent != current) {
            style.append(emptySymbol.repeat(Math.max(0, left)));
        } else {
            style.append(filledSymbol.repeat(Math.max(0, left)));
        }
        String output;
        if (((int) current) == 0) {
            output = emptySymbol.repeat(totalBars);
        } else
            output = style.toString();
        return output;
    }

    private Tag linkTag(final ArgumentQueue args, final Context context) {
        final String link = args.popOr("<link> tag requires one argument, the link to open.").value();
        return Tag.styling(
                TextColor.fromHexString("#5495ff"),
                TextDecoration.UNDERLINED,
                ClickEvent.openUrl(link),
                HoverEvent.showText(Component.text("Open " + link).color(TextColor.fromHexString("#89a5ff")))
        );
    }

    private Tag effectsTag(final ArgumentQueue args, final Context context) {
        final String effect = args.popOr("<effect> tag requires one argument, the text effect.").value();

        TextColor color = switch (effect) {
            case "rainbow" -> TextColor.color(255, 255, 254);
            case "pixel_rainbow" -> TextColor.color(255, 255, 253);
            case "wavy" -> TextColor.color(255, 255, 252);
            case "wavy_letters" -> TextColor.color(255, 255, 251);
            default -> throw new IllegalStateException("Unexpected value: " + effect);
        };
        return Tag.styling(color);
    }

    public Component plugin(String text,Object... data){
        return text("<green>[<yellow>Ticket System</yellow>] <gray>➵</gray>  " + text.formatted(data));
    }


}
