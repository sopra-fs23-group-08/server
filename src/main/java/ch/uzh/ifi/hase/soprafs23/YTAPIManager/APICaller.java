package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;


public class APICaller {

    public static String getApiKey()  {
        String jsonString = "";
        try (FileReader in = new FileReader("src/main/resources/YouTube.ApiKey")) {
            int c;
            while ((c = in.read()) != -1)
                jsonString += (char) c;
            in.close();
        } catch(Exception e){
            throw new IllegalStateException("Something went wrong with the YouTube API key: " + e);
        }
        return jsonString;
    }

    static final String API_KEY = getApiKey();

    public static String getVideosByPlaylistId(String playlistId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String URL = "https://youtube.googleapis.com/youtube/v3/playlistItems?" +
                "&part=snippet" +
                "&playlistId=" + playlistId +
                "&key=" + API_KEY +
                "&maxResults=" + 10;
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }
    
    public static String getRelatedVideos(String videoId, Language language) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String languageString = language != null ? "&relevanceLanguage=" + language.getISO_639_1_Code() : "";
        String URL = "https://youtube.googleapis.com/youtube/v3/search?" +
                "&part=snippet" +
                "&relatedToVideoId=" + videoId +
                "&key=" + API_KEY +
                "&maxResults=" + 10 +
                "&safeSearch=" + "moderate" +
                "&type=" + "video" +
                languageString;
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }
    
    //example location (37.42307,-122.08427)
    public static String getVideosByLocation(String location, Language language)
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String languageString = language != null ? "&relevanceLanguage=" + language.getISO_639_1_Code() : "";
        String URL = "https://youtube.googleapis.com/youtube/v3/search?" +
                "&part=snippet" +
                "&location=" + encodeValue(location) +
                "&locationRadius=" + "250km" +
                "&key=" + API_KEY +
                "&maxResults=" + 10 +
                "&safeSearch=" + "moderate" +
                "&type=" + "video" +
                languageString;
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }
    
    public static String getVideosByQuery(String query, Language language) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String languageString = language != null ? "&relevanceLanguage=" + language.getISO_639_1_Code() : "";
        String URL = "https://youtube.googleapis.com/youtube/v3/search?" +
                "&part=snippet" +
                "&q=" + encodeValue(query) +
                "&key=" + API_KEY +
                "&maxResults=" + 30 +
                "&safeSearch=" + "moderate" +
                "&type=" + "video" +
                languageString;
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }
    
    public static String getCommentsByVideoId(String videoId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String URL = "https://youtube.googleapis.com/youtube/v3/commentThreads?" +
                "&part=snippet" +
                "&videoId=" + videoId +
                "&key=" + API_KEY +
                "&maxResults=" + 100 +
                "&order=" + "relevance";
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }

    public static String getVideoInfoByVideoId(String videoId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String URL = "https://youtube.googleapis.com/youtube/v3/videos?" +
                "&part=statistics,contentDetails" + 
                "&id=" + videoId +
                "&key=" + API_KEY;
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }


    private static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //     // create a client
        HttpClient client = HttpClient.newHttpClient();

        // // create a request
        // HttpRequest request = HttpRequest.newBuilder(
        //     URI.create("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"))
        // .header("accept", "application/json")
        // .build();

        // // use the client to send the request
        // var response = client.send(request, new JsonBodyHandler<>(APOD.class));

        // // the response:
        // System.out.println(response.body().get().title);

        // HttpClient client = HttpClient.newBuilder()
        //     .version(Version.HTTP_1_1)
        //     .followRedirects(Redirect.NORMAL)
        //     .connectTimeout(Duration.ofSeconds(20))
        //     .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
        //     .authenticator(Authenticator.getDefault())
        //         .build();

        HttpRequest request = HttpRequest.newBuilder(
                URI.create("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"))
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
    
}












//json mapping stuff
// class some {
//     public static void main(String[] args)
//             throws IOException, URISyntaxException, ExecutionException, InterruptedException {
//         UncheckedObjectMapper uncheckedObjectMapper = new UncheckedObjectMapper();

//         HttpRequest request = HttpRequest.newBuilder(new URI("https://jsonplaceholder.typicode.com/todos/1"))
//                 .header("Accept", "application/json")
//                 .build();

//         Model model = HttpClient.newHttpClient()
//                 .sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                 .thenApply(HttpResponse::body)
//                 .thenApply(uncheckedObjectMapper::readValue)
//                 .get();

//         System.out.println(model);

//     }
// }



// class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
//         /**
//          * Parses the given JSON string into a Map.
//          */
//         Model readValue(String content) {
//             try {
//                 return this.readValue(content, new TypeReference<Model>() {
//                 });
//             } catch (IOException ioe) {
//                 throw new CompletionException(ioe);
//             }
//         }

// }

// class Model {
//         @Override
//     public String toString() {
//         return "Model [userId=" + userId + ", id=" + id + ", title=" + title + ", completed=" + completed + "]";
//     }
//         private String userId;
//         public String getUserId() {
//             return userId;
//         }
//         public void setUserId(String userId) {
//             this.userId = userId;
//         }
//         public Model(String userId, String id, String title, boolean completed) {
//             this.userId = userId;
//             this.id = id;
//             this.title = title;
//             this.completed = completed;
//         }
//         private String id;

//         public String getId() {
//             return id;
//         }
//         public void setId(String id) {
//             this.id = id;
//         }
//         private String title;
//         public String getTitle() {
//             return title;
//         }
//         public void setTitle(String title) {
//             this.title = title;
//         }
//         private boolean completed;
//         public boolean isCompleted() {
//             return completed;
//         }
//         public void setCompleted(boolean completed) {
//             this.completed = completed;
//         }


//     //getters setters constructors toString
// }

// class JsonBodyHandler<W> implements HttpResponse.BodyHandler<W> {

//     private Class<W> wClass;

//     public JsonBodyHandler(Class<W> wClass) {
//         this.wClass = wClass;
//     }

//     @Override
//     public HttpResponse.BodySubscriber<W> apply(HttpResponse.ResponseInfo responseInfo) {
//         return asJSON(wClass);
//     }

//     public static <T> HttpResponse.BodySubscriber<T> asJSON(Class<T> targetType) {
//         HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

//         return HttpResponse.BodySubscribers.mapping(
//                 upstream,
//                 (String body) -> {
//                     try {
//                         ObjectMapper objectMapper = new ObjectMapper();
//                         return objectMapper.readValue(body, targetType);
//                     } catch (IOException e) {
//                         throw new UncheckedIOException(e);
//                     }
//                 });
//     }
// }