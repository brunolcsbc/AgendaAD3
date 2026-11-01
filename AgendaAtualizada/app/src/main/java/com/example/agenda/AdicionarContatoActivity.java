package com.example.agenda;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.controller.ContatoController;
import com.example.agenda.model.Contato;

public class AdicionarContatoActivity extends AppCompatActivity {
    private EditText editNome, editEmail, editTelefone;
    private Button btnSalvar;
    private ImageView imageViewFoto;
    private Contato contatoEdicao;
    private ContatoController contatoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_contato);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        contatoController = new ContatoController(this);
        inicializarViews();
        verificarEdicao();
    }

    public void inicializarViews() {
        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editTelefone = findViewById(R.id.editTextTelefone);
        btnSalvar = findViewById(R.id.btnSalvar);
        imageViewFoto = findViewById(R.id.imageView); // ID correto do formulário
        btnSalvar.setOnClickListener(v -> salvarContato());
    }

    public void salvarContato() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        if (nome.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Nome e telefone são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contatoEdicao != null) {
            int linhas = contatoController.atualizarContato(
                    contatoEdicao.getId(), nome, email, telefone, contatoEdicao.getFoto());
            if (linhas == 0) {
                Toast.makeText(this, "Não foi possível atualizar o contato", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Contato atualizado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            long id = contatoController.adicionarContato(nome, email, telefone, "");
            if (id == -1) {
                Toast.makeText(this, "Não foi possível adicionar o contato", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void verificarEdicao() {
        if (getIntent().hasExtra("contato")) {
            contatoEdicao = (Contato) getIntent().getSerializableExtra("contato");
            if (contatoEdicao != null) {
                editNome.setText(contatoEdicao.getNome());
                editEmail.setText(contatoEdicao.getEmail());
                editTelefone.setText(contatoEdicao.getTelefone());
                Toolbar toolbar = findViewById(R.id.toolbar2);
                toolbar.setTitle("Editar contato");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contatoController != null) contatoController.close();
    }
}
