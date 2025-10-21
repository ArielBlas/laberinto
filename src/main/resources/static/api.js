class LaberintoAPI {
  constructor() {
    this.baseURL = "http://localhost:8080/api/laberinto";
    this.currentMazeId = null;
  }

  // Generar laberinto
  async generarLaberinto(ancho, alto, algoritmo) {
    try {
      const formData = new URLSearchParams();
      formData.append("ancho", ancho);
      formData.append("alto", alto);
      formData.append("algoritmo", algoritmo);

      const response = await fetch(`${this.baseURL}/generar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      const data = await response.json();
      this.currentMazeId = data.id;
      return data;
    } catch (error) {
      console.error("Error generando laberinto:", error);
      throw error;
    }
  }

  // Resolver laberinto
  async resolverLaberinto(algoritmo) {
    if (!this.currentMazeId) {
      throw new Error("No hay laberinto generado");
    }

    try {
      const formData = new URLSearchParams();
      formData.append("laberintoId", this.currentMazeId);
      formData.append("algoritmo", algoritmo);

      const response = await fetch(`${this.baseURL}/resolver`, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error resolviendo laberinto:", error);
      throw error;
    }
  }

  // Obtener algoritmos disponibles
  async obtenerAlgoritmos() {
    try {
      const response = await fetch(`${this.baseURL}/algoritmos`);
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Error obteniendo algoritmos:", error);
      throw error;
    }
  }

  // Diagnosticar laberinto
  async diagnosticarLaberinto() {
    if (!this.currentMazeId) {
      throw new Error("No hay laberinto generado");
    }

    try {
      const response = await fetch(
        `${this.baseURL}/diagnostico/${this.currentMazeId}`
      );
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      return await response.text();
    } catch (error) {
      console.error("Error diagnosticando laberinto:", error);
      throw error;
    }
  }
}

// Instancia global de la API
const api = new LaberintoAPI();
