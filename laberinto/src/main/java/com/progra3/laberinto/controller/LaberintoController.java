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
	//Endpoints para generar con cada algoritmo
    // Generación con PRIM
    @PostMapping(value = "/generar/prim", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LaberintoDto> generarLaberintoPrim(@RequestParam int ancho, @RequestParam int alto) {
            Laberinto laberinto = laberintoServicio.generarLaberinto(ancho, alto, "PRIM");
            LaberintoDto laberintoDto = convertirALaberintoDto(laberinto);
            return ResponseEntity.status(HttpStatus.CREATED).body(laberintoDto);
    }

    // Generación con KRUSKAL
    @PostMapping(value = "/generar/kruskal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generarLaberintoKruskal(@RequestParam int ancho, @RequestParam int alto) {

            Laberinto laberinto = laberintoServicio.generarLaberinto(ancho, alto, "KRUSKAL");
            LaberintoDto laberintoDto = convertirALaberintoDto(laberinto);
            return ResponseEntity.status(HttpStatus.CREATED).body(laberintoDto);
    }
    
    
    //Endpoints para resolver laberintos con distintos algoritmos
    // Resolver con BFS
    @PostMapping(value = "/resolver/bfs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultadoResolucionDto> resolverLaberintoBfs(@RequestParam String laberintoId) {

            List<Celda> camino = laberintoServicio.resolverLaberinto(laberintoId, "BFS");
            ResultadoResolucionDto resultadoDto = convertirAResultadoDto(camino, "BFS");
            return ResponseEntity.status(HttpStatus.OK).body(resultadoDto);
    }

    // Resolver con DFS
    @PostMapping(value = "/resolver/dfs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultadoResolucionDto> resolverLaberintoDfs(@RequestParam String laberintoId) {

            List<Celda> camino = laberintoServicio.resolverLaberinto(laberintoId, "DFS");
            ResultadoResolucionDto resultadoDto = convertirAResultadoDto(camino, "DFS");
            return ResponseEntity.status(HttpStatus.OK).body(resultadoDto);
    }

    // Resolver con DIJKSTRA
    @PostMapping(value = "/resolver/dijkstra", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultadoResolucionDto> resolverLaberintoDijkstra(@RequestParam String laberintoId) {
            List<Celda> camino = laberintoServicio.resolverLaberinto(laberintoId, "DIJKSTRA");
            ResultadoResolucionDto resultadoDto = convertirAResultadoDto(camino, "DIJKSTRA");
            return ResponseEntity.status(HttpStatus.OK).body(resultadoDto);
    }
 
	@GetMapping(value = "/algoritmos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> obtenerAlgoritmos() {
		List<String> algoritmos = Arrays.asList("BFS", "DFS", "DIJKSTRA", "PRIM", "KRUSKAL");
		return ResponseEntity.status(HttpStatus.OK).body(algoritmos);
	}
	

	// Métodos de conversión -------------------------------------------------- 
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