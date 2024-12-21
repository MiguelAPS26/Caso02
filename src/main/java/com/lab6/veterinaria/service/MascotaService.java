
package com.lab6.veterinaria.service;

import com.lab6.veterinaria.model.Mascota;
import com.lab6.veterinaria.repository.MascotaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository repository;

    public List<Mascota> listar() {
        return repository.findAll();
    }

    public Mascota guardar(Mascota mascota) {
        return repository.save(mascota);
    }

    public void eliminar(int id) {
        repository.deleteById(id);
    }

    public Mascota obtenerPorId(int id) {
    return repository.findById(id).orElse(null);
  }
}

