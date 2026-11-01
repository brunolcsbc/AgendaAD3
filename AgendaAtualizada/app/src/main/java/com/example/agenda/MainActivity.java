package com.example.agenda;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.controller.ContatoController;
import com.example.agenda.model.Contato;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton fabAdicionar;
    private List<Contato> contatos;
    private ArrayAdapter<Contato> adapter;
    private ContatoController controller;
    private SharedPreferences preferencias;
    private TextView txtOrdenacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        preferencias = getSharedPreferences("configuracoes", MODE_PRIVATE);
        controller = new ContatoController(this);
        inicializarViews();
        configurarListeners();
        carregarContatos();
    }

    private void inicializarViews() {
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);
        fabAdicionar = findViewById(R.id.fabAdicionar);
        txtOrdenacao = findViewById(R.id.txtOrdenacao);

        fabAdicionar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AdicionarContatoActivity.class)));
        txtOrdenacao.setOnClickListener(v -> escolherOrdenacao());
    }

    private void escolherOrdenacao() {
        String[] opcoes = {"Nome A–Z", "Nome Z–A"};
        String atual = preferencias.getString("ordenacao", "AZ");
        int selecionado = atual.equals("ZA") ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Ordenar contatos")
                .setSingleChoiceItems(opcoes, selecionado, (dialog, escolha) -> {
                    String ordem = escolha == 1 ? "ZA" : "AZ";
                    preferencias.edit().putString("ordenacao", ordem).apply();
                    carregarContatos();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void carregarContatos() {
        String ordem = preferencias.getString("ordenacao", "AZ");
        txtOrdenacao.setText(ordem.equals("ZA") ? "Ordenação: Nome Z–A  ▾" : "Ordenação: Nome A–Z  ▾");
        contatos = controller.listarContatos(ordem);

        adapter = new ArrayAdapter<Contato>(this, R.layout.list_item, contatos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }
                Contato contato = getItem(position);
                TextView textNome = convertView.findViewById(R.id.textNomeLista);
                TextView textTelefone = convertView.findViewById(R.id.textTelLista);
                if (contato != null) {
                    textNome.setText(contato.getNome());
                    textTelefone.setText(contato.getTelefone());
                }
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    private void configurarListeners() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            Intent intent = new Intent(MainActivity.this, AdicionarContatoActivity.class);
            intent.putExtra("contato", contato);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação de exclusão")
                    .setMessage("Deseja realmente excluir o contato " + contato.getNome() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        controller.apagarContato(contato.getId());
                        carregarContatos();
                        Toast.makeText(this, "Contato excluído", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null && preferencias != null) carregarContatos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controller != null) controller.close();
    }
}
