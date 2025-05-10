/**
 * Main JavaScript file for the Pharmacy Management System
 */

document.addEventListener("DOMContentLoaded", () => {
  // Auto-hide alerts after 5 seconds
  setTimeout(() => {
    const alerts = document.querySelectorAll(".alert")
    alerts.forEach((alert) => {
      const bsAlert = new bootstrap.Alert(alert)
      bsAlert.close()
    })
  }, 5000)

  // Initialize tooltips
  const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
  tooltipTriggerList.map((tooltipTriggerEl) => new bootstrap.Tooltip(tooltipTriggerEl))

  // Initialize popovers
  const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
  popoverTriggerList.map((popoverTriggerEl) => new bootstrap.Popover(popoverTriggerEl))

  // Confirm delete actions
  const deleteButtons = document.querySelectorAll(".btn-delete")
  deleteButtons.forEach((button) => {
    button.addEventListener("click", (e) => {
      if (!confirm("Êtes-vous sûr de vouloir supprimer cet élément ?")) {
        e.preventDefault()
      }
    })
  })

  // Toggle sidebar on mobile
  const sidebarToggle = document.getElementById("sidebarToggle")
  if (sidebarToggle) {
    sidebarToggle.addEventListener("click", () => {
      document.body.classList.toggle("sidebar-toggled")
      document.getElementById("sidebar").classList.toggle("show")
    })
  }

  // Handle barcode scanner input
  const barcodeInput = document.getElementById("barcodeInput")
  if (barcodeInput) {
    let lastCode = ""
    let reading = false
    let readTimer = null

    document.addEventListener("keydown", (e) => {
      // Check if we're in an input field that's not the barcode input
      if (document.activeElement.tagName === "INPUT" && document.activeElement.id !== "barcodeInput") {
        return
      }

      // If Enter key is pressed and we have a barcode
      if (e.key === "Enter" && reading) {
        if (lastCode.length > 3) {
          // Minimum barcode length
          barcodeInput.value = lastCode

          // Submit the form if it exists
          const form = document.getElementById("barcodeForm")
          if (form) {
            form.submit()
          }
        }

        reading = false
        lastCode = ""
        clearTimeout(readTimer)
        e.preventDefault()
        return
      }

      // Start reading or continue reading
      if (!reading) {
        reading = true
        lastCode = ""

        // Set a timeout to reset if there's a pause in scanning
        readTimer = setTimeout(() => {
          reading = false
          lastCode = ""
        }, 200)
      }

      // Add character to code if it's a valid character
      if (e.key.length === 1) {
        lastCode += e.key
        clearTimeout(readTimer)

        // Reset the timeout
        readTimer = setTimeout(() => {
          reading = false
          lastCode = ""
        }, 200)
      }
    })
  }
})
