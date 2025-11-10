document.addEventListener("DOMContentLoaded", () => {
  const user = JSON.parse(localStorage.getItem("loggedUser"));
  const welcomeMessage = document.getElementById("welcomeMessage");
  const logoutBtn = document.getElementById("logoutBtn");

  // Simulação de dados iniciais
  const dados = {
    veiculos: 12,
    vendas: 5,
    clientes: 8,
    fornecedores: 3
  };

  if (user) {
    welcomeMessage.textContent = `Bem-vindo, ${user.username} (${user.role}) 👋`;
  } else {
    window.location.href = "../index.html"; // força login
  }

  // Preenche os cards
  document.getElementById("totalVeiculos").textContent = dados.veiculos;
  document.getElementById("totalVendas").textContent = dados.vendas;
  document.getElementById("totalClientes").textContent = dados.clientes;
  document.getElementById("totalFornecedores").textContent = dados.fornecedores;

  // Logout
  logoutBtn.addEventListener("click", () => {
    localStorage.removeItem("loggedUser");
    window.location.href = "../index.html";
  });
});
