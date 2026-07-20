const { contextBridge } = require('electron')

contextBridge.exposeInMainWorld('doctorHelpDesktop', {
  platform: process.platform,
  isDesktop: true,
})

