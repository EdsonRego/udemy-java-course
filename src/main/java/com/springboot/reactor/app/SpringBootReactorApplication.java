package com.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		ejemploCollectList();
//		ejemploToString();
//		ejemploFlatMap();
//		ejemploIterable();
	}
	
	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Pedro", "Fulano"));
		usuariosList.add(new Usuario("Maria", "Fulana"));
		usuariosList.add(new Usuario("Thiago", "Sultano"));
		usuariosList.add(new Usuario("Edson", "Rego"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Wills"));

		Flux.fromIterable(usuariosList)
		.collectList()
		.subscribe(lista -> {
			lista.forEach(item -> log.info(item.toString()));
			});
	}

	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Pedro", "Fulano"));
		usuariosList.add(new Usuario("Maria", "Fulana"));
		usuariosList.add(new Usuario("Thiago", "Sultano"));
		usuariosList.add(new Usuario("Edson", "Rego"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Wills"));

		Flux.fromIterable(usuariosList).map(
				usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("Bruce".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				}).map(nombre -> {
					return nombre.toLowerCase();
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploFlatMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("Pedro Fulano");
		usuariosList.add("Maria Fulana");
		usuariosList.add("Thiago Sultano");
		usuariosList.add("Edson Rego");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Wills");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("Bruce")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploIterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("Pedro Fulano");
		usuariosList.add("Maria Fulana");
		usuariosList.add("Thiago Sultano");
		usuariosList.add("Edson Rego");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Wills");

		Flux<String> nombres = Flux.fromIterable(
				usuariosList); /*
								 * Flux.just("Andres Guzman","Pedro Fulano","Maria Fulana","Thiago Sultano",
								 * "Juan Mengano", "Edson Rego","Bruce Lee", "Bruce Wills");
								 */

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce")).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombres no puedem ser vacíos");
					}

					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));

				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecución del observable con éxito");
			}
		});

	}

}
