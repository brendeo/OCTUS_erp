export function OctopusLogo({ className = 'h-20 w-20' }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 120 120" fill="none" aria-hidden>
      <circle cx="60" cy="48" r="18" fill="#0d9488" />
      <ellipse cx="60" cy="52" rx="8" ry="6" fill="#0f766e" opacity="0.4" />
      <path
        d="M42 62 Q60 78 78 62"
        stroke="#0d9488"
        strokeWidth="4"
        fill="none"
        strokeLinecap="round"
      />
      {[
        [32, 58, 18, 50],
        [48, 68, 40, 82],
        [72, 68, 80, 82],
        [88, 58, 102, 50],
        [38, 52, 24, 44],
        [82, 52, 96, 44],
        [52, 48, 44, 32],
        [68, 48, 76, 32],
      ].map(([x1, y1, x2, y2], i) => (
        <path
          key={i}
          d={`M${x1} ${y1} L${x2} ${y2}`}
          stroke="#0d9488"
          strokeWidth="3"
          strokeLinecap="round"
        />
      ))}
      <circle cx="52" cy="44" r="3" fill="white" />
      <circle cx="68" cy="44" r="3" fill="white" />
    </svg>
  )
}
