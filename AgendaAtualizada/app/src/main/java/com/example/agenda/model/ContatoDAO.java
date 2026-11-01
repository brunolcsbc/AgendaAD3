package com.example.agenda.model;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {

    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper; //nossa classe helper SQLite

    //construtor - instancia o helper
    public ContatoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    //abre o database para escrita

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    //fecha o database

    public void close() {
        databaseHelper.close();
    }

    //metodo para inserir novo constato

    public long adicionarContato(Contato contato) {

        //cria um objeto values para os valores a serem inseridos

        ContentValues values = new ContentValues();
        values.put("nome", contato.getNome());
        values.put("email", contato.getEmail());
        values.put("telefone", contato.getTelefone());
        values.put("foto", contato.getFoto());

        //metodo para inserir - parametros nome da tabela "contato" e os valores

        return database.insert("contato", null, values);

    }

    //metodo para SELECT - retornando uma lista dos contatos buscados

    public List<Contato> listarContatos() {
        return listarContatos("AZ");
    }

    public List<Contato> listarContatos(String ordem) {

        List<Contato> contatos = new ArrayList<>();

        //cursor para percorrer os resultados - query na tabela "contato"
        String ordenarPor = "ZA".equals(ordem) ? "nome DESC" : "nome ASC";
        Cursor cursor = database.query("contato", null, null, null, null, null, ordenarPor);

        //cursor no inicio

        if (cursor.moveToFirst()) {
            do {//repete

                Contato contato = new Contato();//cria uma instancia de Contato com os valores

                contato.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                contato.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                contato.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                contato.setTelefone(cursor.getString(cursor.getColumnIndexOrThrow("telefone")));
                contato.setFoto(cursor.getString(cursor.getColumnIndexOrThrow("foto")));

                contatos.add(contato); //adiciona o contato à lista

            } while (cursor.moveToNext());//cursor para o proximo

        }

        cursor.close();

        return contatos; //retorna a lista

    }

    //metodo para fazer o update de um contato

    public int atualizarContato(Contato contato) {

        ContentValues values = new ContentValues();

        values.put("nome", contato.getNome());
        values.put("email", contato.getEmail());
        values.put("telefone", contato.getTelefone());
        values.put("foto", contato.getFoto());

        //update na tabela "contato" com os valores
        return database.update("contato", values, "id=?", new String[]{String.valueOf(contato.getId())});

    }

    //metodo para delete pelo id

    public void apagarContato(int id) {

        //delete na tabela "contato"
        database.delete("contato", "id=?", new String[]{String.valueOf(id)});

    }

    //metodo para SELECT por um id - retorna um contato

    public Contato buscaContatoPorId(int id) {

        Cursor cursor = database.query("contato", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            Contato contato = new Contato();

            contato.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            contato.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
            contato.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            contato.setTelefone(cursor.getString(cursor.getColumnIndexOrThrow("telefone")));
            contato.setFoto(cursor.getString(cursor.getColumnIndexOrThrow("foto")));

            cursor.close();

            return contato;

        }

        return null;

    }
}
