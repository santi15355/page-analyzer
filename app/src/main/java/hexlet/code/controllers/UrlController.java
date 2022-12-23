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
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;


public final class UrlController {
    public static Handler addUrl = ctx -> {
        String userUrl = ctx.formParam("url");

        String[] schemes = {"http", "https", "ftp"};
        UrlValidator urlValidator = new UrlValidator(schemes);

        if (!urlValidator.isValid(userUrl)) {
            ctx.sessionAttribute("flash", "Некоретный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        } else {
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
        }

    };

    public static Handler showUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        final int entriesPerPage = 10;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(page * entriesPerPage)
                .setMaxRows(entriesPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;

        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .toList();

        ctx.attribute("urls", urls);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
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



