document.addEventListener("DOMContentLoaded", () => {
    // Dataset para Ventas y Pedidos
    const dataVentas = {
        title: "REPORTE DE VENTAS",
        btnText: "tabla de reporte de ventas",
        headers: ["Día", "Ventas Realizadas", "Monto Total (S/)"],
        values: [25, 18, 12, 22, 15, 22, 28],
        tableRows: [
            ["Lunes", "25", "S/ 1,250.00"],
            ["Martes", "18", "S/ 890.00"],
            ["Miércoles", "12", "S/ 540.00"],
            ["Jueves", "22", "S/ 1,100.00"],
            ["Viernes", "15", "S/ 720.00"],
            ["Sábado", "22", "S/ 1,080.00"],
            ["Domingo", "28", "S/ 1,400.00"]
        ]
    };

    const dataPedidos = {
        title: "REPORTE DE PEDIDOS",
        btnText: "tabla de reporte de pedidos",
        headers: ["Día", "Pedidos Recibidos", "Pedidos Completados"],
        values: [10, 14, 8, 16, 20, 25, 30],
        tableRows: [
            ["Lunes", "10", "9"],
            ["Martes", "14", "12"],
            ["Miércoles", "8", "8"],
            ["Jueves", "16", "15"],
            ["Viernes", "20", "18"],
            ["Sábado", "25", "22"],
            ["Domingo", "30", "27"]
        ]
    };

    const labels = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

    // Inicialización de Chart.js
    const ctx = document.getElementById("salesChart").getContext("2d");
    const chart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Métrica",
                data: dataVentas.values,
                backgroundColor: "#3b2cb4",
                borderRadius: 4,
                barThickness: 35
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { display: false, beginAtZero: true },
                x: {
                    grid: { display: false },
                    ticks: { color: "#3b2cb4", font: { weight: "bold" } }
                }
            }
        }
    });

    // Función para renderizar la tabla y textos
    function cargarMetrica(data) {
        document.getElementById("chartTitle").innerText = data.title;
        document.getElementById("accordionBtnText").innerText = `+ Expandir ${data.btnText}`;

        // Render Encabezados
        const headerRow = document.getElementById("tableHeaderRow");
        headerRow.innerHTML = data.headers.map(h => `<th>${h}</th>`).join("");

        // Render Cuerpo de la Tabla
        const tableBody = document.getElementById("tableBody");
        tableBody.innerHTML = data.tableRows.map(row => `
            <tr>
                <td>${row[0]}</td>
                <td>${row[1]}</td>
                <td>${row[2]}</td>
            </tr>
        `).join("");

        // Actualizar gráfico
        chart.data.datasets[0].data = data.values;
        chart.update();
    }

    // Event listener para el select
    const reportFilter = document.getElementById("reportFilter");
    reportFilter.addEventListener("change", (e) => {
        if (e.target.value === "pedidos") {
            cargarMetrica(dataPedidos);
        } else {
            cargarMetrica(dataVentas);
        }
    });

    // Lógica para desplegar el acordeón
    const btnToggleTable = document.getElementById("btnToggleTable");
    const tableContent = document.getElementById("tableContent");
    btnToggleTable.addEventListener("click", () => {
        tableContent.classList.toggle("open");
    });

    // Carga inicial (Ventas)
    cargarMetrica(dataVentas);
});