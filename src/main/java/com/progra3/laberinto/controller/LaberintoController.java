package com.progra3.laberinto.controller;

import com.progra3.laberinto.dto.LaberintoDto;
import com.progra3.laberinto.dto.ResultadoResolucionDto;
import com.progra3.laberinto.model.Celda;
import com.progra3.laberinto.model.Laberinto;
import com.progra3.laberinto.service.LaberintoServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/laberinto")
@CrossOrigin(origins = "*")
public class LaberintoController {

	private final LaberintoServicio laberintoServicio;

	public LaberintoController(LaberintoServicio laberintoServicio) {
		this.laberintoServicio = laberintoServicio;
	}
	//cambiar cada endpoint por cada algoritmo.

	@PostMapping(value = "/generar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LaberintoDto> generarLaberinto(@RequestParam int ancho, @RequestParam int alto,
			@RequestParam String algoritmo) {
		LaberintoDto laberintoDto = convertirALaberintoDto(laberintoServicio.generarLaberinto(ancho, alto, algoritmo));
		return ResponseEntity.status(HttpStatus.CREATED).body(laberintoDto);
	}

	@PostMapping(value = "/resolver", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResultadoResolucionDto> resolverLaberinto(@RequestParam String laberintoId,
			@RequestParam String algoritmo) {

		ResultadoResolucionDto resultadoDto = convertirAResultadoDto(
				laberintoServicio.resolverLaberinto(laberintoId, algoritmo), algoritmo);
		return ResponseEntity.status(HttpStatus.OK).body(resultadoDto);
	}

	@GetMapping(value = "/algoritmos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> obtenerAlgoritmos() {
		List<String> algoritmos = Arrays.asList("BFS", "DFS", "DIJKSTRA", "PRIM", "KRUSKAL", "GREEDY");
		return ResponseEntity.status(HttpStatus.OK).body(algoritmos);
	}
	
	@GetMapping(value = "/diagnostico/{laberintoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> diagnosticarLaberinto(@PathVariable String laberintoId) {
		try {
			System.out.println("=== DIAGNÓSTICO LABERINTO ===");
			System.out.println("Laberinto ID: " + laberintoId);
			
			// Verificar celdas
			var celdas = laberintoServicio.getNeo4jServicio().obtenerCeldasPorLaberinto(laberintoId).collectList().block();
			System.out.println("Celdas encontradas: " + (celdas != null ? celdas.size() : "null"));
			
			// Verificar inicio
			var inicio = laberintoServicio.getNeo4jServicio().obtenerCeldaInicio(laberintoId).block();
			System.out.println("Celda inicio: " + (inicio != null ? "SÍ" : "NO"));
			
			// Verificar salida
			var salida = laberintoServicio.getNeo4jServicio().obtenerCeldaSalida(laberintoId).block();
			System.out.println("Celda salida: " + (salida != null ? "SÍ" : "NO"));
			
			String resultado = String.format("Laberinto ID: %s, Celdas: %d, Inicio: %s, Salida: %s", 
				laberintoId, 
				celdas != null ? celdas.size() : 0,
				inicio != null ? "SÍ" : "NO",
				salida != null ? "SÍ" : "NO");
			
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		} catch (Exception e) {
			System.out.println("ERROR en diagnóstico: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	// Métodos de conversión
	private LaberintoDto convertirALaberintoDto(Laberinto laberinto) {
		return new LaberintoDto(laberinto.getId(), laberinto.getAncho(), laberinto.getAlto(), laberinto.getGrid(),
				laberinto.getCeldaInicio(), laberinto.getCeldaSalida());
	}

	private ResultadoResolucionDto convertirAResultadoDto(List<Celda> camino,
			String algoritmo) {
		// Obtener métricas reales del algoritmo usado
		int celdasExploradas = laberintoServicio.obtenerCeldasExploradas(algoritmo);
		long tiempoEjecucion = laberintoServicio.obtenerTiempoEjecucion(algoritmo);

		return new ResultadoResolucionDto(camino, celdasExploradas, tiempoEjecucion, algoritmo, !camino.isEmpty());
	}
}