package com.example.agenda.controller;

import android.content.Context;
import com.example.agenda.model.Contato;
import com.example.agenda.model.ContatoDAO;
import java.util.List;

public class ContatoController {
    private ContatoDAO contatoDAO;

    public ContatoController(Context context) {
        contatoDAO = new ContatoDAO(context);
        contatoDAO.open();
    }

    public long adicionarContato(String nome, String email, String telefone, String foto) {
        // A ordem do construtor de Contato é nome, telefone, email, foto.
        Contato contato = new Contato(nome, telefone, email, foto);
        return contatoDAO.adicionarContato(contato);
    }

    public List<Contato> listarContatos() {
        return contatoDAO.listarContatos();
    }

    public List<Contato> listarContatos(String ordem) {
        return contatoDAO.listarContatos(ordem);
    }

    public int atualizarContato(int id, String nome, String email, String telefone, String foto) {
        Contato contato = new Contato(id, nome, telefone, email, foto);
        return contatoDAO.atualizarContato(contato);
    }

    public void apagarContato(int id) {
        contatoDAO.apagarContato(id);
    }

    public void close() {
        contatoDAO.close();
    }
}
