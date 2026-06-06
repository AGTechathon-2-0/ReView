// Google Apps Script endpoint code for Google Sheets sync
// Usage:
// 1. Go to Google Sheets -> Extensions -> Apps Script
// 2. Paste this code.
// 3. Deploy -> New Deployment -> Select "Web App"
// 4. Set "Who has access" to "Anyone"
// 5. Copy the generated Web App URL and paste it into the EcoTag app settings

function doPost(e) {
  try {
    const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
    const data = JSON.parse(e.postData.contents);
    
    // Check if columns exist
    if (sheet.getLastRow() === 0) {
      const headers = [
        "id", "timestamp", "lat", "lng", "temperature", "humidity", "pressure",
        "gas", "uv", "iaq_score", "iaq_status", "iaq_color", "heat_index",
        "comfort_status", "uv_status", "uv_advice", "weather_trend",
        "health_advice", "alert", "session_id", "device_id"
      ];
      sheet.appendRow(headers);
    }

    const rows = [];
    data.forEach(function(item) {
      rows.push([
        item.id, item.timestamp, item.lat, item.lng,
        item.temperature, item.humidity, item.pressure,
        item.gas, item.uv, item.iaq_score, item.iaq_status, item.iaq_color,
        item.heat_index, item.comfort_status, item.uv_status, item.uv_advice,
        item.weather_trend, item.health_advice, item.alert,
        item.session_id, item.device_id
      ]);
    });

    // Bulk append
    const startRow = sheet.getLastRow() + 1;
    sheet.getRange(startRow, 1, rows.length, rows[0].length).setValues(rows);

    return ContentService.createTextOutput(JSON.stringify({ 
      "status": "success",
      "synced_count": rows.length
    })).setMimeType(ContentService.MimeType.JSON);
    
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({ 
      "status": "error", "message": error.toString() 
    })).setMimeType(ContentService.MimeType.JSON);
  }
}
