"use client"

import React from "react"
import { motion, type Variants } from "framer-motion"
import { cn } from "@/lib/utils"

export interface AnimatedGroupProps {
  children: React.ReactNode
  className?: string
  variants?: {
    container?: Variants
    item?: Variants
  }
  preset?: "fade" | "slide" | "scale"
}

// Gentle, calm fade-and-slide motion (no spring overshoot, no bounce) as per UX Architecture
const defaultContainerVariants: Variants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1,
      delayChildren: 0.05,
    },
  },
}

const defaultItemVariants: Variants = {
  hidden: { opacity: 0, y: 12 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.35,
      ease: [0.25, 0.1, 0.25, 1], // Gentle ease-out
    },
  },
}

export function AnimatedGroup({
  children,
  className,
  variants,
}: AnimatedGroupProps) {
  const container = variants?.container || defaultContainerVariants
  const item = variants?.item || defaultItemVariants

  return (
    <motion.div
      initial="hidden"
      animate="visible"
      variants={container}
      className={cn(className)}
    >
      {React.Children.map(children, (child) => {
        if (!React.isValidElement(child)) return child
        return <motion.div variants={item}>{child}</motion.div>
      })}
    </motion.div>
  )
}
