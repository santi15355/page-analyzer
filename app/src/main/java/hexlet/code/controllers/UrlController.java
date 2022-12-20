package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

        List<UrlCheck> urlChecks = new QUrlCheck()
                .url.equalTo(url)
                .orderBy().id.desc()
                .findList();

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", urlChecks);
        ctx.render("show.html");
    };

    public static Handler checkUrl = ctx -> {
        long id = Long.parseLong(ctx.pathParam("id"));

        Url url = Objects.requireNonNull(new QUrl()
                .id.equalTo(id)
                .findOne());

        try {
            HttpResponse<String> response = Unirest
                    .get(url.getName())
                    .asString();

            String content = response.getBody();

            Document body = Jsoup.parse(content);

            int statusCode = response.getStatus();
            String title = body.title();
            String h1 = body.selectFirst("h1") != null
                    ? Objects.requireNonNull(body.selectFirst("h1")).text()
                    : null;
            String description = body.selectFirst("meta[name=description]") != null
                    ? Objects.requireNonNull(body.selectFirst("meta[name=description]")).attr("content")
                    : null;

            UrlCheck check = new UrlCheck(statusCode, title, h1, description, url);
            check.save();

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Не удалось проверить страницу");
            ctx.sessionAttribute("flash-type", "danger");
        } finally {
            ctx.redirect("/urls/" + id);
        }
    };
}



