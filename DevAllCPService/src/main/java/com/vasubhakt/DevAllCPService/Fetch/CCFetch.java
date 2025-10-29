package com.vasubhakt.DevAllCPService.Fetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Model.CCProfile;
import com.vasubhakt.DevAllCPService.Model.ContestParticipation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CCFetch {

    private final ExecutorService executor;

    private static final String BASE_URL = "https://www.codechef.com/users/";

    public CCProfile fetchProfile(String handle) {
        try {
            // Fetch profile HTML asynchronously
            Future<Document> docFuture = executor.submit(() -> fetchDocument(handle));
            Document doc = docFuture.get();

            // Basic info 
            Element ratingElem = doc.selectFirst(".rating-number");
            Element maxRatingElem = doc.selectFirst(".max-rating-number");
            Element starsElem = doc.selectFirst(".rating-star");

            if (ratingElem == null)
                throw new RuntimeException("Invalid CodeChef handle: " + handle);

            Integer rating = parseIntSafe(ratingElem.text());
            Integer maxRating = (maxRatingElem != null)
                    ? parseIntSafe(maxRatingElem.text().replace("(", "").replace(")", ""))
                    : null;

            // Extract number of stars (★ symbols)
            Integer stars = (starsElem != null)
                    ? (int) starsElem.text().chars().filter(ch -> ch == '★').count()
                    : null;

            // Problems solved
            Element solvedElem = doc.selectFirst(".problems-solved");
            Integer problemsSolved = 0;
            if (solvedElem != null) {
                Elements headers = solvedElem.select("h5");
                for (Element h5 : headers) {
                    String text = h5.text().replaceAll("[^0-9]", "");
                    if (!text.isEmpty()) problemsSolved += Integer.parseInt(text);
                }
            }

            // Contest history
            List<ContestParticipation> contests = new ArrayList<>();
            Elements contestRows = doc.select("table.dataTable tbody tr");
            for (Element row : contestRows) {
                Elements cols = row.select("td");
                if (cols.size() >= 4) {
                    contests.add(new ContestParticipation(
                            cols.get(0).text(), // contest code
                            cols.get(1).text(), // contest name
                            null, // rank not visible directly
                            null, // old rating not visible
                            parseIntSafe(cols.get(2).text()), // new rating
                            cols.get(3).text() // end date
                    ));
                }
            }

            return new CCProfile(
                    handle,
                    rating,
                    maxRating,
                    stars,
                    problemsSolved,
                    contests,
                    Collections.emptyMap() // heatmap not available
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Document fetchDocument(String handle) throws IOException {
        String url = BASE_URL + handle;
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; DevAllCPService/1.0; +https://github.com/vasubhakt)")
                .timeout(10000)
                .get();
    }

    private Integer parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
