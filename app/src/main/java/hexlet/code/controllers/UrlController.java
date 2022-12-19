package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public final class UrlController {
    public static Handler addUrl = ctx -> {
        String userUrl = ctx.formParam("url");
        assert userUrl != null;
        URL urlParser = new URL(userUrl);
        String modifiedUrl = urlParser.getProtocol() + "://" + urlParser.getAuthority();

        Url urlFromDB = new QUrl()
                .name.equalTo(modifiedUrl)
                .findOne();

        if (null != urlFromDB) {
            ctx.sessionAttribute("flash", "Сайт уже добавлен!");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls");
            return;
        }
        Url url = new Url(modifiedUrl);
        url.save();

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");

    };

    public static Handler showUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;
        int offset = (page - 1) * rowsPerPage;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(offset)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        ctx.attribute("urls", urls);
        ctx.attribute("page", page);
        ctx.render("showUrls.html");
    };

    public static Handler showUrl = ctx -> {
        long id = Long.parseLong(ctx.pathParam("id"));

        Url url = Objects.requireNonNull(new QUrl()
                .id.equalTo(id)
                .findOne());

        ctx.attribute("url", url);
        ctx.render("show.html");
    };
}



