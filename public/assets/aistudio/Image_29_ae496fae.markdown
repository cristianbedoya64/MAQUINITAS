---
name: Royal Arcade Bingo
colors:
  surface: '#180c2f'
  surface-dim: '#180c2f'
  surface-bright: '#3f3357'
  surface-container-lowest: '#13062a'
  surface-container-low: '#211538'
  surface-container: '#25193c'
  surface-container-high: '#2f2347'
  surface-container-highest: '#3b2e53'
  on-surface: '#ebddff'
  on-surface-variant: '#d8c3ad'
  inverse-surface: '#ebddff'
  inverse-on-surface: '#362a4e'
  outline: '#a08e7a'
  outline-variant: '#534434'
  surface-tint: '#ffb95f'
  primary: '#ffc174'
  on-primary: '#472a00'
  primary-container: '#f59e0b'
  on-primary-container: '#613b00'
  inverse-primary: '#855300'
  secondary: '#ffb0cd'
  on-secondary: '#640039'
  secondary-container: '#aa0266'
  on-secondary-container: '#ffbad3'
  tertiary: '#54ddfc'
  on-tertiary: '#003640'
  tertiary-container: '#29c1df'
  on-tertiary-container: '#004b58'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffddb8'
  primary-fixed-dim: '#ffb95f'
  on-primary-fixed: '#2a1700'
  on-primary-fixed-variant: '#653e00'
  secondary-fixed: '#ffd9e4'
  secondary-fixed-dim: '#ffb0cd'
  on-secondary-fixed: '#3e0022'
  on-secondary-fixed-variant: '#8c0053'
  tertiary-fixed: '#acedff'
  tertiary-fixed-dim: '#4cd7f6'
  on-tertiary-fixed: '#001f26'
  on-tertiary-fixed-variant: '#004e5c'
  background: '#180c2f'
  on-background: '#ebddff'
  surface-variant: '#3b2e53'
typography:
  headline-xl:
    fontFamily: Rubik
    fontSize: 40px
    fontWeight: '800'
    lineHeight: 48px
  headline-xl-mobile:
    fontFamily: Rubik
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 38px
  headline-lg:
    fontFamily: Rubik
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
  headline-md:
    fontFamily: Rubik
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
  headline-sm:
    fontFamily: Rubik
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  card-number:
    fontFamily: Rubik
    fontSize: 20px
    fontWeight: '800'
    lineHeight: 20px
  card-letter:
    fontFamily: Rubik
    fontSize: 15px
    fontWeight: '900'
    lineHeight: 18px
    letterSpacing: 1px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Rubik
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 18px
    letterSpacing: 0.5px
  label-md:
    fontFamily: Rubik
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.25px
  label-sm:
    fontFamily: Rubik
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 12px
    letterSpacing: 0.5px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 0.75rem
  gutter-tablet: 1rem
  margin: 1rem
  margin-tablet: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1rem
  space-xl: 1.5rem
  space-2xl: 2rem
---

## Brand & Style

This design system channels the electrifying energy of modern digital arcade halls fused with the regal prestige of high-stakes live bingo palaces. Built specifically for mobile-first players, the visual language balances high-velocity excitement with extreme legibility.

### Brand Personality & Core Emotions
- **Festive & Exuberant:** Every interaction feels celebratory. Audio-visual rewards, sparkling win states, and energetic tactile feedback simulate the physical rush of a crowded gaming parlor.
- **Prestige & Value:** Deep jewel tones paired with radiant metallic golds evoke jackpot luxury rather than cheap novelty.
- **Clarity Under Pressure:** In rapid-fire multi-card play, visual noise is strictly controlled. High-contrast typography and unmistakable daub states eliminate player cognitive fatigue.

### Design Movement: Neon-Glass Arcade
The aesthetic combines **Glassmorphism** and **Tactile/Arcade Skeuomorphism**:
- Rich, dimensional backgrounds with ambient radial light leaks.
- Glossy, multi-stop pill buttons with translucent glass top-bevel highlights.
- Neon glow outlines layered over dark, saturated indigo surfaces to signal active game phases, jackpot countdowns, and ball drops.

## Colors

The palette is tuned for high-contrast dark environments, establishing an atmospheric midnight arcade backdrop punctuated by brilliant neon game elements and gleaming metallic incentives.

### Palette Architecture
- **Canvas & Structural Bases (Neutral):**
  - Base Floor: `#120924` (ultra-deep twilight indigo)
  - Surface Default: `#1C1033` (deep royal purple)
  - Surface Raised / Containers: `#2A1752` (mid royal purple)
  - Card & Modal Overlay: `rgba(42, 23, 82, 0.75)` with 20px backdrop blur
- **Jackpot & Currency Accents (Primary Gold):**
  - Radiant Gold: `#FFD700` (sparkles, mega-jackpot text, high-tier trophies)
  - Amber Gold: `#F59E0B` (primary CTA fills, coin counters, active streak meters)
  - Deep Bronze Shade: `#92400E` (tactile button bevel shadows)
- **High-Velocity Highlights & Action (Secondary Magenta & Tertiary Cyan):**
  - Electric Magenta: `#EC4899` (dauber ink, high-energy alerts, special event triggers)
  - Hyper Cyan: `#06B6D4` (live ball callouts, timer pulses, secondary interactive badges)
- **Bingo Play Card Palette (Surface Light):**
  - Card Face: `#FFFDF7` (warm cream matrix surface)
  - Number Text (Unmarked): `#1E1035` (maximum contrast against light card surface)
  - Daubed Cell: `#EC4899` with `#FFFFFF` text and inner drop-shadow
  - Free Space / Star Cell: `#F59E0B` with radiant star emblem

### Application Rules
- Use `#FFD700` and `#F59E0B` exclusively for conversion drivers, jackpot totals, and claiming prizes.
- Reserve `#06B6D4` and `#EC4899` for live gameplay signals (incoming balls, power-ups, daubs).
- Never place un-shadowed neon text directly over light cards.

## Typography

The type system blends the bouncy, playful geometry of **Rubik** for all numerals, headings, and high-impact arcade labels with the clarity and humanist readability of **Plus Jakarta Sans** for player instructions, game rules, and transactional micro-copy.

### Hierarchy & Game Matrix Roles
- **Numerical Dominance:** Numbers on play cards and falling game balls must command instant recognition. `card-number` utilizes an ultra-heavy 800 weight with strict monospaced tabular figure metrics so numerals never jitter during countdowns or ball rolls.
- **Column Identification (`card-letter`):** B-I-N-G-O headers run across the top of matrices in heavy, spaced uppercase glyphs matched to their respective column neon hues.
- **Jackpot Counters:** Use `headline-xl` or `headline-xl-mobile` with gold radial text gradients and subtle drop-shadows to emphasize current prize pool values.

## Layout & Spacing

Layouts are engineered for **one-handed thumb ergonomics** on modern mobile viewports (375px to 430px base portrait width), ensuring primary game-play buttons, power-up triggers, and cards remain within the natural lower arc of movement.

### Thumb-Zone Ergonomics & Viewport Partitioning
- **Top Zone (HUD & Caller Tube):** Occupies the upper 20% of screen height. Houses caller tube, jackpot pools, power-up meters, and pause/profile triggers. Non-touch or low-frequency interactions.
- **Center Stage (The Playfield):** Occupies 50–55% of vertical space. Contains single or split dual-card configurations with maximized cell touch targets (minimum 44x44pt per cell).
- **Lower Action Deck (Primary Thumb Reach):** Occupies the bottom 25–30%. Houses the giant "BINGO!" callout trigger, instant auto-daub boosts, and horizontal carousel selectors for multi-card switching.

### Grid & Adaptation
- **Mobile (portrait):** 4-column fluid layout, `margin`: 16px (`1rem`), `gutter`: 12px (`0.75rem`).
- **Tablet / Large Foldable:** Fluid 8-column layout. Multi-card grids pin side-by-side rather than stacked, keeping cards large and legible while reserving the right rail for one-thumb action triggers.

## Elevation & Depth

Visual depth combines glossy tactile surfaces with multi-tiered purple ambient shadows and radiant neon under-glows.

### Depth Layers
- **Level 0 (Atmospheric Backdrop):** Deep radial gradient `#2A1752` fading to `#120924`, accented with faint floating ambient particles (bokeh sparkles and soft floating coins).
- **Level 1 (Card Frames & Floating HUD Shelves):** Solid `#1C1033` bases coated with a 1px perimeter border of `rgba(255, 255, 255, 0.12)`. Casts a soft ambient drop shadow: `0 8px 24px rgba(0, 0, 0, 0.45)`.
- **Level 2 (Active Play Matrix / Cream Cards):** Crisp `#FFFDF7` boards elevated above the dark purple table. Casts a composite double shadow: `0 4px 6px -1px rgba(0,0,0,0.3), 0 12px 20px -4px rgba(0,0,0,0.5)`.
- **Level 3 (Tactile Arcade Controls & Flying Balls):** Buttons feature a dual-layer extrusion:
  - Top edge highlight: `inset 0 1.5px 0 0 rgba(255, 255, 255, 0.45)`
  - Bottom bevel anchor: `inset 0 -3px 0 0 rgba(0, 0, 0, 0.35)`
  - Floor drop shadow: `0 6px 16px rgba(245, 158, 11, 0.4)`
- **Level 4 (Jackpot & Win Overlay):** Full-screen frosted wash: `rgba(18, 9, 36, 0.85)` with `backdrop-filter: blur(12px)`. Trophies, cash blasts, and dialogs emerge with cyan and gold rim lighting: `box-shadow: 0 0 32px rgba(255, 215, 0, 0.35)`.

## Shapes

The shape system adopts a pill-shaped and squoval foundation (`roundedness: 3`). Soft, friendly contours make game components look bounceable, playful, and safe, reducing visual rigidity.

### Shape Allocations
- **Action Buttons & Pill Chips:** Full pill geometry (`border-radius: 9999px`).
- **Bingo Play Card Enclosures:** High curvature squovals (`rounded-xl` / 24px to 32px), creating a physical token-like silhouette.
- **Card Matrix Number Cells:** Rounded squovals (`rounded-md` / 8px to 12px) allowing circular daub stamp marks to overlay cleanly without clipping cell boundaries.
- **Ball Callers & Token Badges:** Absolute 1:1 circular spheres with interior radial highlights simulating lacquered physical plastic balls.

## Components

### Buttons
- **Primary Arcade Action ("BINGO!" / Claim Prize):**
  - Pill-shaped container filled with a multi-stop gradient from `#FFD700` (top) to `#F59E0B` (bottom).
  - Upper rim glass highlight (`inset 0 2px 0 rgba(255,255,255,0.6)`), 4px physical bottom bevel (`#92400E`).
  - Active press state shifts the element downward by 2px with an inner shadow to simulate a physical micro-switch click.
- **Secondary Game Utility (Power-Up / Boost):**
  - Translucent purple fill (`rgba(42, 23, 82, 0.8)`) outlined with a 1.5px border of `#06B6D4`.
  - Neon cyan drop glow (`0 0 12px rgba(6, 182, 212, 0.4)`).
  - Text rendered in bold white `label-lg`.

### Bingo Cards
- **Matrix Architecture:** 5x5 grid mounted on a cream-white (`#FFFDF7`) slate with a contrasting header bar.
- **Column Header Strip:** Five rounded segments tinted with arcade neon accents:
  - B: `#EC4899` (Magenta)
  - I: `#06B6D4` (Cyan)
  - N: `#F59E0B` (Gold)
  - G: `#10B981` (Emerald)
  - O: `#8B5CF6` (Violet)
- **Grid Cells:** Off-white squares (`#F4F1EA`) with rounded corners. Numbers set in `Rubik 800` (`#1E1035`).
- **Daubed Cells:** On player tap, the cell transitions dynamically into an ink stamp mark (solid `#EC4899` starburst or translucent gem orb), transforming the number to `#FFFFFF` with an explosive micro-particle burst.

### Ball Caller Tube (Top HUD)
- Circular glossy sphere carousel rolling from right to left across a dark indigo slot.
- Active (Current) Ball: Scaled 1.25x larger than queue balls, rimmed with a pulsating `#06B6D4` or `#EC4899` glow ring.
- Surface highlights use an asymmetrical off-center white radial gradient to mimic physical sheen.

### Chips & Mini Counters
- Fully rounded pill tokens housing coin stacks, gems, and power-up tallies.
- Background: Translucent dark violet (`rgba(28, 16, 51, 0.7)`) with high-contrast colored icon prefixes (Gold coin, Pink lightning bolt).
- Typography: `label-md` in crisp white.

### Modals & Dialogs (Jackpot / Victory Screen)
- Backdrop: Deep dark indigo blur (`rgba(18, 9, 36, 0.8)` with 16px blur).
- Card: Deep royal purple surface (`#2A1752`) framed by a metallic gold border (`2px solid #FFD700`).
- Crowned with celebratory art (confetti, animated coin vortex), anchored at the bottom with a full-width high-gloss gold primary CTA button.

### Input Fields & Search (Lobby / Rooms)
- Pill-shaped inset containers with dark surface fills (`#160B29`).
- Subtle 1px border in `rgba(255, 255, 255, 0.15)`, shifting to `#06B6D4` glow on focus.
- Placeholder text in soft lavender (`rgba(255, 255, 255, 0.4)`).