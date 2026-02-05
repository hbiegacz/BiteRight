"use client"

export function BotanicalPattern() {
  // Subtle botanical leaf pattern in a slightly darker shade of the off-white background
  // Using oklch(0.92 0.02 145) which is close to --muted but with green tint
  const patternColor = "rgba(45, 75, 55, 0.04)" // Very subtle dark green
  
  return (
    <div 
      className="pointer-events-none fixed inset-0 z-0"
      style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fillRule='evenodd'%3E%3Cg fill='${encodeURIComponent(patternColor)}'%3E%3Cpath d='M7 2c1.5 2.5 3 6 2 10-1-1-3-2-5-2 1-3 2-6 3-8zm0 0c-1 2-2 5-1 8-2 0-4 1-5 2 0-4 2-7.5 6-10z'/%3E%3Cpath d='M52 8c-1.5 2.5-3 6-2 10 1-1 3-2 5-2-1-3-2-6-3-8zm0 0c1 2 2 5 1 8 2 0 4 1 5 2 0-4-2-7.5-6-10z'/%3E%3Cpath d='M30 25c2 3 4 7.5 3 12.5-1.5-1-3.5-2.5-6-2.5 1-4 2-7.5 3-10zm0 0c-1 2.5-2 6-1 10-2.5 0-4.5 1.5-6 2.5 0-5 2.5-9.5 7-12.5z'/%3E%3Cpath d='M15 45c1 2 2.5 5 2 8-1-.5-2.5-1.5-4-1.5.5-2.5 1.5-5 2-6.5zm0 0c-1 1.5-1.5 4-1 6.5-1.5 0-3 1-4 1.5 0-3 1.5-6 5-8z'/%3E%3Cpath d='M45 42c1 2 2.5 5 2 8-1-.5-2.5-1.5-4-1.5.5-2.5 1.5-5 2-6.5zm0 0c-1 1.5-1.5 4-1 6.5-1.5 0-3 1-4 1.5 0-3 1.5-6 5-8z'/%3E%3Ccircle cx='10' cy='28' r='1.5'/%3E%3Ccircle cx='50' cy='32' r='1'/%3E%3Ccircle cx='25' cy='52' r='1'/%3E%3Ccircle cx='38' cy='15' r='1.5'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
        backgroundRepeat: 'repeat',
        backgroundSize: '60px 60px',
      }}
      aria-hidden="true"
    />
  )
}

export function BotanicalPatternAlt() {
  // Alternative pattern with more delicate vine/tendril motif
  const patternColor = "rgba(45, 75, 55, 0.035)"
  
  return (
    <div 
      className="pointer-events-none fixed inset-0 z-0"
      style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg width='80' height='80' viewBox='0 0 80 80' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' stroke='${encodeURIComponent(patternColor)}' strokeWidth='1'%3E%3Cpath d='M10 5 Q15 15, 10 25 Q5 35, 10 45' strokeLinecap='round'/%3E%3Cpath d='M10 25 Q18 22, 22 15' strokeLinecap='round'/%3E%3Cpath d='M10 25 Q2 22, -2 18' strokeLinecap='round'/%3E%3Cpath d='M70 35 Q65 45, 70 55 Q75 65, 70 75' strokeLinecap='round'/%3E%3Cpath d='M70 55 Q62 52, 58 45' strokeLinecap='round'/%3E%3Cpath d='M70 55 Q78 52, 82 48' strokeLinecap='round'/%3E%3Cpath d='M40 60 Q45 50, 40 40 Q35 30, 40 20' strokeLinecap='round'/%3E%3Cpath d='M40 40 Q48 43, 52 50' strokeLinecap='round'/%3E%3Cpath d='M40 40 Q32 43, 28 50' strokeLinecap='round'/%3E%3Ccircle cx='22' cy='15' r='2' fill='${encodeURIComponent(patternColor)}' stroke='none'/%3E%3Ccircle cx='58' cy='45' r='2' fill='${encodeURIComponent(patternColor)}' stroke='none'/%3E%3Ccircle cx='52' cy='50' r='1.5' fill='${encodeURIComponent(patternColor)}' stroke='none'/%3E%3Ccircle cx='28' cy='50' r='1.5' fill='${encodeURIComponent(patternColor)}' stroke='none'/%3E%3C/g%3E%3C/svg%3E")`,
        backgroundRepeat: 'repeat',
        backgroundSize: '80px 80px',
      }}
      aria-hidden="true"
    />
  )
}
