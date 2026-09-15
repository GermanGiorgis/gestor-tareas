module.exports = {
  stylesheet: ["pdf-style.css"],
  body_class: ["doc"],
  pdf_options: {
    format: "A4",
    printBackground: true,
    margin: { top: "18mm", bottom: "16mm", left: "20mm", right: "20mm" },
    displayHeaderFooter: true,
    headerTemplate: "<div></div>",
    footerTemplate:
      '<div style="width:100%; font-size:8.5px; color:#888; padding:0 20mm; display:flex; justify-content:space-between; font-family: Arial, sans-serif;">' +
      '<span>Gestor de Tareas — Documentación del Proyecto</span>' +
      '<span>Página <span class="pageNumber"></span> de <span class="totalPages"></span></span>' +
      "</div>",
  },
};
