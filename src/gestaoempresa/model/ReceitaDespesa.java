package gestaoempresa.model;

import java.util.Date;

public class ReceitaDespesa {

    private int id;
    private String tipo;
    private String descricao;
    private double valor;
    private Date data;
    
    public ReceitaDespesa() {
    }
    public ReceitaDespesa(int id, String tipo, String descricao, double valor, Date data) {
        this.id = id;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }
    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }
}
