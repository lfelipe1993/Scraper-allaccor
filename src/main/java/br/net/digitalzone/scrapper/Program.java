package br.net.digitalzone.scrapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

public class Program {
	private static final String CHAT_ID = "-1001449884585";
	//private static final String CHAT_ID = "-1001317699407";//test
    private static final String TOKEN = "1620921748:AAH3Xm-qANhi_ZQcF-VFIbYoUgKgRUq6dFk";

	public static void main(String[] args) {


		// String url =
		// "https://limitlessexperiences.accor.com/2-tickets-paris-saint-germain-lens-borelli-stand-access-323-3-november-2024";
		String url = "https://limitlessexperiences.accor.com/le-ai-gpsp-vp-112024";

		Document page;

		try {
			// First, make an initial request to the website to get the cookies
			Connection.Response initialResponse = Jsoup
					.connect("https://limitlessexperiences.accor.com/amcookie/cookie/allow")
					.method(Connection.Method.GET).execute();

			// Get the cookies from the response
			Map<String, String> cookies = initialResponse.cookies();
			Map<String, String> headers = initialResponse.headers();

			// Now, send a POST request with the form data and the cookies
			Connection.Response loginResponse = Jsoup
					.connect("https://limitlessexperiences.accor.com/switcher/post/index/").data("currency", "BRL")
					.data("delivery", "BR").data("lan", "accor_samerica_pt_br").data("path", "le-ai-gpsp-vp-112024")
					.cookies(cookies) // Pass the cookies to the request
					.method(Connection.Method.POST).execute();

			// Update the cookie store with any new cookies sent by the server during login
			cookies.putAll(loginResponse.cookies());

			// Now you can access pages that require authentication using the cookies
			Document doc = Jsoup.connect("https://limitlessexperiences.accor.com/le-ai-gpsp-vp-112024").cookies(cookies) // Use
																															// cookies
																															// for
																															// authentication
					.followRedirects(true).get();

			// Do something with the document, like parsing the HTML
			String pag = doc.title();

			String stockOk = doc.getElementsByClass("stock").text();

			System.out.println("MSG: " + pag.substring(59, 98) + "\n\n" + stockOk);
			
			
			sendNotificationTL(pag.substring(59, 98) + "\n\n" + stockOk);
			WhatsToUser(pag.substring(59, 98) + "\n\n" + stockOk);
			
			if(stockOk != "Fora de estoque") {
				CallToUser();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void WhatsToUser(String text) {

		List<Usuarios> usuariosList = Stream.of(Usuarios.values()).collect(Collectors.toList());

		usuariosList.stream().forEach(x -> {
			System.out.println(x.getKeyForWhats());
			String msg = "Olá " + x.getNome() + "\n\n" + text;

			String user = x.getPhoneNumber();

			UriBuilder builder = UriBuilder.fromUri("https://api.callmebot.com/whatsapp.php")
					.queryParam("phone", x.getPhoneNumber()).queryParam("text", msg)
					.queryParam("apikey", x.getKeyForWhats());

			// System.out.println(builder);

			JobToUser(builder, x.getNome());
		});

		System.exit(1);

	}
	
	public static void CallToUser() {

		UriBuilder builder = UriBuilder.fromUri("https://api.callmebot.com/start.php")
				.queryParam("user", "@lfelipe93").queryParam("text", "Olá Luiz, O Ticket Formula 1 está Disponivel Agora").queryParam("lang", "pt-BR-Standard-A")
				.queryParam("rpt", "2");
		
		System.out.println(builder);

		JobToUser(builder, "@lfelipe93");
		
		System.exit(1);

}
	
	

	public static void JobToUser(UriBuilder builder, String user) {

		// block until complete
		Response res = null;
		try {
			Client client = ClientBuilder.newClient();

			Future<Response> future = client.target(builder).request().async().get();

			res = future.get(10, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			System.out.println("TimeOut na requisição");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			if (res != null) {
				res.close();
			}
		}

		if (res != null) {
			System.out.println("Status of the job for " + user + " : " + res.getStatus());
		} else {
			System.out.println("Não foi possível enviar mensagem para o user " + user);
		}
	}
	
public static void sendNotificationTL(String message) {
    	

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", CHAT_ID)
                .queryParam("text", message)
                .queryParam("parse_mode", "html")
        		.queryParam("disable_web_page_preview", "true");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + TOKEN))
                .timeout(Duration.ofSeconds(5))
                .build();
        
        HttpResponse<String> response = null;
        
		try {
			response = client
			  .send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//System.out.println(request.toString());
        //System.out.println(response.statusCode());
        //System.out.println(response.body());
    }

}
