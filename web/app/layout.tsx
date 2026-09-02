import type { Metadata } from "next"
import "./globals.css"

export const metadata: Metadata = {
  title: "VitalSense (सेहतसेतु) — ग्रामीण और दूरदराज क्षेत्रों के लिए स्वास्थ्य साथी",
  description: "Rural, offline-first telemedicine and vital signs monitoring for patients, ASHA workers, and doctors.",
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="hi">
      <body className="antialiased font-sans bg-[#FAFAF7] text-[#1C1C1C]">
        {children}
      </body>
    </html>
  )
}
