// Menu toggle
document.addEventListener('DOMContentLoaded', () => {
    const menuBtn = document.querySelectorAll('.menu-btn');
    const sidebar = document.querySelectorAll('.sidebar');

    menuBtn.forEach((btn, index) => {
        btn.addEventListener('click', () => {
            const body = document.body;
            body.classList.toggle('menu-open');
            sidebar[index].style.display = body.classList.contains('menu-open') ? 'flex' : 'none';
        });
    });
});

// =====================================================
// CRUD SIMULADO PARA TODOS OS MÓDULOS
// =====================================================

const db = {
    clientes: [],
    veiculos: [],
    fornecedores: [],
    funcionarios: [],
    vendas: []
};

let editId = null;
let deleteId = null;
let currentModule = ''; 

// Define campos de cada módulo
const moduleFields = {
    cliente: ['name','email','phone'],
    veiculo: ['model','brand','year'],
    fornecedor: ['name','contact','email'],
    funcionario: ['name','role','email'],
    venda: ['vehicle','client','seller','date','value']
};

// Abrir formulário
function openForm(action, module, id = null) {
    currentModule = module;
    const modal = document.getElementById('formModal');
    modal.style.display = 'block';
    const title = document.getElementById('formTitle');
    const submitBtn = document.getElementById('formSubmitBtn');

    title.innerText = action === 'add' ? `Adicionar ${capitalize(module)}` : `Editar ${capitalize(module)}`;
    submitBtn.innerText = action === 'add' ? 'Adicionar' : 'Salvar';
    editId = action === 'edit' ? id : null;

    // Resetar form
    const form = document.querySelector('form');
    form.reset();

    // Preencher dados se for edição
    if(editId) {
        const item = db[module + 's'].find(i => i.id === id);
        moduleFields[module].forEach(f => {
            const input = document.getElementById(module + capitalize(f));
            if(input) input.value = item[f];
        });
    }
}

// Fechar formulário
function closeForm() {
    document.getElementById('formModal').style.display = 'none';
}

// Abrir popup exclusão
function openDelete(id, module) {
    currentModule = module;
    deleteId = id;
    document.getElementById('deleteModal').style.display = 'block';
}

// Fechar popup exclusão
function closeDelete() {
    deleteId = null;
    document.getElementById('deleteModal').style.display = 'none';
}

// Confirmar exclusão
function confirmDelete() {
    db[currentModule + 's'] = db[currentModule + 's'].filter(i => i.id !== deleteId);
    deleteId = null;
    closeDelete();
    renderTable(currentModule);
}

// Submissão formulário
document.addEventListener('submit', function(e){
    const form = e.target;
    // Allow normal submission for server-backed forms
    if (form.hasAttribute('data-server-form')) {
        return; // do not preventDefault; let Spring handle
    }
    e.preventDefault();
    const module = currentModule;

    const data = {};
    moduleFields[module].forEach(f => {
        const input = document.getElementById(module + capitalize(f));
        if(input) data[f] = input.value.trim();
    });

    if(editId) {
        // editar
        const item = db[module + 's'].find(i => i.id === editId);
        Object.assign(item, data);
    } else {
        // adicionar
        const arr = db[module + 's'];
        const id = arr.length ? arr[arr.length -1].id + 1 : 1;
        arr.push({id, ...data});
    }

    closeForm();
    renderTable(module);
});

// Renderizar tabela
function renderTable(module) {
    const table = document.getElementById(module + 'sTable');
    if(!table) return; // Se não existir na página, não faz nada
    const tbody = table.querySelector('tbody');
    tbody.innerHTML = '';
    db[module + 's'].forEach(item => {
        let row = `<tr><td>${item.id}</td>`;
        moduleFields[module].forEach(f => row += `<td>${item[f]}</td>`);
        row += `<td>
            <button onclick="openForm('edit','${module}',${item.id})">Editar</button>
            <button onclick="openDelete(${item.id},'${module}')">Excluir</button>
        </td></tr>`;
        tbody.innerHTML += row;
    });
}

// Inicializa tabela da página atual
const currentPageModule = document.body.getAttribute('data-module');
if(currentPageModule) renderTable(currentPageModule);

// Utilitário
function capitalize(str){
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// ===========================
// Helpers de moeda (BRL)
// ===========================
function parseCurrencyFlexible(str) {
    if (typeof str !== 'string') return NaN;
    let s = str.trim();
    if (!s) return NaN;
    // Remove símbolos e letras (ex.: R$, espaços)
    s = s.replace(/[^0-9.,-]/g, '');

    const hasComma = s.includes(',');
    const hasDot = s.includes('.');

    if (hasComma && hasDot) {
        const lastComma = s.lastIndexOf(',');
        const lastDot = s.lastIndexOf('.');
        const decimalSep = lastComma > lastDot ? ',' : '.';
        const thousandSep = decimalSep === ',' ? '.' : ',';
        const cleaned = s
            .replace(new RegExp('\\' + thousandSep, 'g'), '')
            .replace(decimalSep, '.');
        return Number(cleaned);
    }

    if (hasComma) {
        // vírgula como decimal, remover pontos de milhar
        const cleaned = s.replace(/\./g, '').replace(',', '.');
        return Number(cleaned);
    }

    // Apenas ponto ou somente dígitos
    const onlyDigitsAndDots = s.replace(/[^0-9.]/g, '');
    const parts = onlyDigitsAndDots.split('.');
    if (parts.length > 2) {
        const dec = parts.pop();
        return Number(parts.join('') + '.' + dec);
    }
    return Number(onlyDigitsAndDots);
}

function onlyDigits(str){
    return (str || '').replace(/\D+/g,'');
}

function formatBRLFromDigits(digits){
    const cents = digits.replace(/^0+/, '') || '0';
    const n = Number(cents) / 100;
    return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function applyMoneyMask(input){
    const digits = onlyDigits(input.value);
    input.value = formatBRLFromDigits(digits);
}

// ===========================
// CATÁLOGO PÚBLICO DE VEÍCULOS
// ===========================
function carregarCatalogoPublico() {
  const lista = document.getElementById("catalogo-list");
  const buscaInput = document.getElementById("buscaCatalogo");
  if (!lista) return;

  let veiculos = JSON.parse(localStorage.getItem("veiculos")) || [];

  function render(filtro = "") {
    const filtrados = veiculos.filter(v => {
      const texto = `${v.marca} ${v.modelo} ${v.ano}`.toLowerCase();
      return texto.includes(filtro.toLowerCase());
    });

    lista.innerHTML = filtrados.length
      ? filtrados.map(v => `
        <div class="card-veiculo">
          <img src="${v.foto || '../assets/img/sem-imagem.jpg'}" alt="${v.modelo}">
          <div class="card-content">
            <h3>${v.marca} ${v.modelo}</h3>
            <p><strong>Ano:</strong> ${v.ano}</p>
            <p><strong>Cor:</strong> ${v.cor || '-'}</p>
            <p class="price">R$ ${parseFloat(v.valor || 0).toFixed(2)}</p>
          </div>
          <div class="card-actions">
            <button onclick="contatoVendedor('${v.contato || 'N/A'}')">Entrar em contato</button>
          </div>
        </div>
      `).join("")
      : `<p>Nenhum veiculo encontrado.</p>`;
  }

  if (buscaInput) {
    buscaInput.addEventListener("input", e => render(e.target.value));
  }

  render();
}

function contatoVendedor(info) {
  if (info === 'N/A') {
    alert("Informação de contato não disponível.");
    return;
  }
  alert(`Entre em contato com o vendedor: ${info}`);
}

document.addEventListener("DOMContentLoaded", carregarCatalogoPublico);

// ===========================
// EDIÇÃO DE VEÍCULO VIA MESMO FORM DO MODAL
// ===========================
function openVehicleCreate() {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  if (title) title.innerText = 'Adicionar Veículo';
  if (submitBtn) submitBtn.innerText = 'Salvar';
  if (form) {
    form.setAttribute('action', '/veiculos/novo');
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');
    form.reset();
    const idInput = form.querySelector('[name="id"]');
    if (idInput) idInput.value = '';
    const valorInput = form.querySelector('input[name="valor"]');
    if (valorInput) valorInput.value = '';
  }
  modal.style.display = 'block';
}

// ===========================
// FORMATAÇÃO DE VALOR NA LISTA DE VEÍCULOS
// ===========================
document.addEventListener('DOMContentLoaded', () => {
  const formatTableCurrency = (selector) => {
    const cells = document.querySelectorAll(selector);
    cells.forEach(td => {
      const raw = (td.textContent || '').toString().trim();
      const num = parseCurrencyFlexible(raw);
      if (isNaN(num)) return;
      td.textContent = num.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    });
  };
  formatTableCurrency('#veiculosTable td.valor-cell');
  formatTableCurrency('#vendasTable td.valor-cell');

  // Ajustar especificamente a coluna de RESULTADO em vendas para garantir sinal negativo antes do símbolo
  const resultCells = document.querySelectorAll('#vendasTable td.result-cell');
  resultCells.forEach(td => {
    const diffAttr = td.getAttribute('data-diff');
    if (!diffAttr) return;
    let num = Number(diffAttr);
    if (isNaN(num)) num = parseCurrencyFlexible(diffAttr);
    const abs = Math.abs(num);
    const brl = abs.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    td.textContent = (num < 0 ? '-' : '') + brl.replace(/^-/, '');
  });
});

// ===========================
// FORMATAÇÃO DE CPF/CNPJ NA LISTAGEM DE FORNECEDORES
// ===========================
function formatCPFFromDigits(digits) {
  const d = onlyDigits(String(digits || ''));
  if (d.length !== 11) return digits;
  return d.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
}

function formatCNPJFromDigits(digits) {
  const d = onlyDigits(String(digits || ''));
  if (d.length !== 14) return digits;
  return d.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5');
}

document.addEventListener('DOMContentLoaded', () => {
  const docCells = document.querySelectorAll('#fornecedoresTable td.doc-cpf, #fornecedoresTable td.doc-cnpj, #clientesTable td.doc-cpf, #clientesTable td.doc-cnpj, #funcionariosTable td.doc-cpf');
  if (!docCells.length) return;
  docCells.forEach(td => {
    const txt = (td.textContent || '').toString().trim();
    if (!txt) return;
    if (td.classList.contains('doc-cpf')) {
      td.textContent = formatCPFFromDigits(txt);
    } else if (td.classList.contains('doc-cnpj')) {
      td.textContent = formatCNPJFromDigits(txt);
    }
  });
});

function toggleVehicleSearch() {
  const panel = document.getElementById('vehicleSearchPanel');
  if (!panel) return;
  panel.style.display = (panel.style.display === 'none' || panel.style.display === '') ? 'block' : 'none';
}

function toggleSupplierSearch() {
  const panel = document.getElementById('supplierSearchPanel');
  if (!panel) return;
  panel.style.display = (panel.style.display === 'none' || panel.style.display === '') ? 'block' : 'none';
}

function toggleClientSearch() {
  const panel = document.getElementById('clientSearchPanel');
  if (!panel) return;
  panel.style.display = (panel.style.display === 'none' || panel.style.display === '') ? 'block' : 'none';
}

function toggleEmployeeSearch() {
  const panel = document.getElementById('employeeSearchPanel');
  if (!panel) return;
  panel.style.display = (panel.style.display === 'none' || panel.style.display === '') ? 'block' : 'none';
}

function toggleSaleSearch() {
  const panel = document.getElementById('saleSearchPanel');
  if (!panel) return;
  panel.style.display = (panel.style.display === 'none' || panel.style.display === '') ? 'block' : 'none';
}

function openCarEdit(anchorEl) {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  const d = anchorEl.dataset;

  if (title) title.innerText = 'Atualizar Veículo';
  if (submitBtn) submitBtn.innerText = 'Atualizar';

  if (form) {
    form.setAttribute('action', `/veiculos/atualizar/${d.placa}`);
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');

    const setField = (name, value) => {
      const input = form.querySelector(`[name="${name}"]`);
      if (input) input.value = value || '';
    };

    setField('id', d.placa);
    setField('marca', d.marca);
    setField('modelo', d.modelo);
    setField('ano', d.ano);
    setField('cor', d.cor);
    setField('placa', d.placa);
    setField('chassi', d.chassi);
    setField('fornecedor.id', d.fornecedorId);
    // garantir máscara de placa após preencher via código
    const placaInput = form.querySelector('input[name="placa"]');
    if (placaInput) {
      placaInput.value = maskPlacaTextValue(placaInput.value);
    }
    setField('valor', d.valor);
    setField('observacao', d.observacao);
    setField('fornecedor', d.fornecedor);
    // aplicar máscara visual ao campo de valor
    const valorInput = form.querySelector('input[name="valor"]');
    if (valorInput) {
      const num = parseCurrencyFlexible(String(d.valor || ''));
      if (!isNaN(num)) {
        valorInput.value = num.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
      }
    }
  }

  modal.style.display = 'block';
}

// ===========================
// Máscara de PLACA (AAA-0A00)
// ===========================
function maskPlacaTextValue(value) {
  const raw = (value || '').toString().toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 7);
  return raw.length > 3 ? raw.slice(0, 3) + '-' + raw.slice(3) : raw;
}

// Garantir que o campo placa no modal fique mascarado ao abrir/editar
document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  if (!form) return;
  const placaInput = form.querySelector('input[name="placa"]');
  if (placaInput) {
    // Reaplicar máscara se valor vier sem hífen (ex.: carga de dataset)
    placaInput.value = maskPlacaTextValue(placaInput.value);
  }
});

// ===========================
// Máscara de moeda em inputs do formulário
// ===========================
document.addEventListener('input', (e) => {
  const el = e.target;
  if (!(el instanceof HTMLInputElement)) return;
  if (!el.classList.contains('money-input')) return;
  applyMoneyMask(el);
});

document.addEventListener('focus', (e) => {
  const el = e.target;
  if (!(el instanceof HTMLInputElement)) return;
  if (!el.classList.contains('money-input')) return;
  applyMoneyMask(el);
}, true);

document.addEventListener('blur', (e) => {
  const el = e.target;
  if (!(el instanceof HTMLInputElement)) return;
  if (!el.classList.contains('money-input')) return;
  applyMoneyMask(el);
}, true);

// Converter valor mascarado antes de enviar ao servidor (sem impedir submit)
document.addEventListener('submit', (e) => {
  const form = e.target;
  if (!(form instanceof HTMLFormElement)) return;
  if (!form.hasAttribute('data-server-form')) return;

  // Confirmação para edição (atualização)
  try {
    const action = String(form.getAttribute('action') || '');
    if (action.includes('/veiculos/atualizar/') || action.includes('/fornecedores/atualizar/') || action.includes('/clientes/atualizar/') || action.includes('/funcionarios/atualizar/')) {
      const ok = window.confirm('Tem certeza que deseja salvar as alterações?');
      if (!ok) {
        e.preventDefault();
        return;
      }
    }
  } catch (_) {}

  // Normalizar CPF/CNPJ (apenas dígitos) antes de enviar
  try {
    const cpf = form.querySelector('input[name="cpf"]');
    if (cpf && cpf.value) {
      cpf.value = (cpf.value || '').replace(/\D+/g, '');
    }
    const cnpj = form.querySelector('input[name="cnpj"]');
    if (cnpj && cnpj.value) {
      cnpj.value = (cnpj.value || '').replace(/\D+/g, '');
    }

    // Se formulário de fornecedor, limpar documento não selecionado
    const action = String(form.getAttribute('action') || '');
    if (action.includes('/fornecedores/') || action.includes('/clientes/')) {
      const tipoRadio = form.querySelector('input[name="tipoPessoa"]:checked');
      if (tipoRadio) {
        if (tipoRadio.value === 'F' && cnpj) {
          cnpj.value = '';
        } else if (tipoRadio.value === 'J' && cpf) {
          cpf.value = '';
        }
      }
    }
  } catch (_) {}

  const moneyInputs = form.querySelectorAll('input.money-input');
  moneyInputs.forEach((inp) => {
    const txt = (inp.value || '').trim();
    const num = parseCurrencyFlexible(txt);
    if (!isNaN(num)) {
      // enviar com ponto decimal (ex.: 56469.87) para o Spring converter em BigDecimal
      const normalized = num.toFixed(2);
      inp.value = normalized;
    }
  });
});

// ===========================
// FORNECEDORES - criar/editar via modal
// ===========================
function openSupplierCreate() {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  if (title) title.innerText = 'Adicionar Fornecedor';
  if (submitBtn) submitBtn.innerText = 'Salvar';
  if (form) {
    form.setAttribute('action', '/fornecedores/novo');
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');
    form.reset();
    // Default: Pessoa Física
    const pfRadio = form.querySelector('input[name="tipoPessoa"][value="F"]');
    if (pfRadio) {
      pfRadio.checked = true;
      if (typeof window.setPessoaType === 'function') window.setPessoaType('F');
    }
  }
  modal.style.display = 'block';
}

function openSupplierEdit(anchorEl) {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  const d = anchorEl.dataset;

  if (title) title.innerText = 'Atualizar Fornecedor';
  if (submitBtn) submitBtn.innerText = 'Atualizar';

  if (form) {
    form.setAttribute('action', `/fornecedores/atualizar/${d.id}`);
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');

    const setField = (name, value) => {
      const input = form.querySelector(`[name="${name}"]`);
      if (input) input.value = value || '';
    };

    setField('nome', d.nome);
    setField('cnpj', d.cnpj);
    setField('cpf', d.cpf);
    setField('telefone', d.telefone);
    setField('email', d.email);
    setField('endereco', d.endereco);

    // Determina tipo de pessoa pela presença de CNPJ
    const isPJ = (d.cnpj || '').toString().trim().length > 0;
    const tipoVal = isPJ ? 'J' : 'F';
    const tipoRadio = form.querySelector(`input[name="tipoPessoa"][value="${tipoVal}"]`);
    if (tipoRadio) {
      tipoRadio.checked = true;
      if (typeof window.setPessoaType === 'function') window.setPessoaType(tipoVal);
    }
  }

  modal.style.display = 'block';
}

// ===========================
// CLIENTES - criar/editar via modal
// ===========================
function openClientCreate() {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  if (title) title.innerText = 'Adicionar Cliente';
  if (submitBtn) submitBtn.innerText = 'Salvar';
  if (form) {
    form.setAttribute('action', '/clientes/novo');
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');
    form.reset();
    const pfRadio = form.querySelector('input[name="tipoPessoa"][value="F"]');
    if (pfRadio) {
      pfRadio.checked = true;
      if (typeof window.setPessoaTypeCliente === 'function') window.setPessoaTypeCliente('F');
    }
  }
  modal.style.display = 'block';
}

function openClientEdit(anchorEl) {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  const d = anchorEl.dataset;

  if (title) title.innerText = 'Atualizar Cliente';
  if (submitBtn) submitBtn.innerText = 'Atualizar';

  if (form) {
    form.setAttribute('action', `/clientes/atualizar/${d.id}`);
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');

    const setField = (name, value) => {
      const input = form.querySelector(`[name="${name}"]`);
      if (input) input.value = value || '';
    };

    setField('nome', d.nome);
    setField('cnpj', d.cnpj);
    setField('cpf', d.cpf);
    setField('telefone', d.telefone);
    setField('email', d.email);
    setField('endereco', d.endereco);

    const isPJ = (d.cnpj || '').toString().trim().length > 0;
    const tipoVal = isPJ ? 'J' : 'F';
    const tipoRadio = form.querySelector(`input[name="tipoPessoa"][value="${tipoVal}"]`);
    if (tipoRadio) {
      tipoRadio.checked = true;
      if (typeof window.setPessoaTypeCliente === 'function') window.setPessoaTypeCliente(tipoVal);
    }
  }

  modal.style.display = 'block';
}

// ===========================
// FUNCIONÁRIOS - criar/editar via modal
// ===========================
function openEmployeeCreate() {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  if (title) title.innerText = 'Adicionar Funcionário';
  if (submitBtn) submitBtn.innerText = 'Salvar';
  if (form) {
    form.setAttribute('action', '/funcionarios/novo');
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');
    form.reset();
  }
  modal.style.display = 'block';
}

function openEmployeeEdit(anchorEl) {
  const modal = document.getElementById('formModal');
  if (!modal) return;
  const form = modal.querySelector('form');
  const title = document.getElementById('formTitle');
  const submitBtn = document.getElementById('formSubmitBtn');
  const d = anchorEl.dataset;

  if (title) title.innerText = 'Atualizar Funcionário';
  if (submitBtn) submitBtn.innerText = 'Atualizar';

  if (form) {
    form.setAttribute('action', `/funcionarios/atualizar/${d.id}`);
    form.setAttribute('method', 'post');
    form.setAttribute('data-server-form', 'true');

    const setField = (name, value) => {
      const input = form.querySelector(`[name="${name}"]`);
      if (input) input.value = value || '';
    };

    setField('nome', d.nome);
    setField('cpf', d.cpf);
    setField('cargo', d.cargo);
    setField('telefone', d.telefone);
    setField('email', d.email);
    setField('endereco', d.endereco);
  }

  modal.style.display = 'block';
}