package com.ey.restaurante.service;

import com.ey.restaurante.model.entity.Reservacion;
import com.ey.restaurante.repository.ReservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservacionDatabaseService implements DatabaseService<Reservacion> {

    private final ReservacionRepository reservacionRepository;

    @Override
    public Reservacion save(Reservacion entity) {
        return reservacionRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservacion> findById(Long id) {
        return reservacionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservacion> findAll() {
        return reservacionRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        reservacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return reservacionRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long countReservacionesActivasPorFecha(LocalDate fecha) {
        return reservacionRepository.countReservacionesActivasPorFecha(fecha);
    }
}
