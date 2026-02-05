"use client"

import { useEffect, useState, useRef } from "react"
import Link from "next/link"
import Image from "next/image"
import { Button } from "@/components/ui/button"
import { ArrowRight, Sparkles, ChevronDown } from "lucide-react"

export function Hero() {
  const [scrollY, setScrollY] = useState(0)
  const [showContent, setShowContent] = useState(false)
  const heroRef = useRef<HTMLElement>(null)

  useEffect(() => {
    const handleScroll = () => {
      setScrollY(window.scrollY)
      // Show content after scrolling 100px
      if (window.scrollY > 100) {
        setShowContent(true)
      }
    }

    window.addEventListener("scroll", handleScroll, { passive: true })
    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  const scrollToContent = () => {
    window.scrollTo({ top: window.innerHeight * 0.8, behavior: "smooth" })
  }

  return (
    <>
      {/* Full-screen hero image section */}
      <section ref={heroRef} className="relative h-screen w-full overflow-hidden">
        {/* Background image */}
        <Image
          src="/images/hero-food.jpg"
          alt="Healthy food lifestyle"
          fill
          className="object-cover"
          priority
          style={{
            transform: `scale(${1 + scrollY * 0.0003}) translateY(${scrollY * 0.3}px)`,
          }}
        />
        
        {/* Dark overlay gradient */}
        <div className="absolute inset-0 bg-gradient-to-b from-primary/60 via-primary/40 to-background" />
        
        {/* Centered scroll indicator */}
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <div 
            className="text-center transition-all duration-700"
            style={{
              opacity: Math.max(0, 1 - scrollY * 0.005),
              transform: `translateY(${scrollY * 0.2}px)`,
            }}
          >
            <h2 className="mb-4 text-lg font-medium uppercase tracking-widest text-primary-foreground/90 drop-shadow-lg sm:text-xl">
              Welcome to BiteRight
            </h2>
            <p className="mb-8 text-primary-foreground/80 drop-shadow-md">
              Scroll down to discover your health journey
            </p>
            <button 
              onClick={scrollToContent}
              className="group flex flex-col items-center text-primary-foreground/80 transition-colors hover:text-primary-foreground"
              aria-label="Scroll down"
            >
              <ChevronDown className="h-8 w-8 animate-bounce" />
            </button>
          </div>
        </div>
      </section>

      {/* Content section that appears on scroll */}
      <section 
        className={`relative bg-background py-20 transition-all duration-1000 sm:py-32 ${
          showContent ? "opacity-100 translate-y-0" : "opacity-0 translate-y-10"
        }`}
      >
        {/* Decorative background elements */}
        <div className="absolute inset-0 -z-10 overflow-hidden">
          <div className="absolute left-1/4 top-1/4 h-72 w-72 rounded-full bg-secondary/20 blur-3xl" />
          <div className="absolute bottom-1/4 right-1/4 h-96 w-96 rounded-full bg-accent/10 blur-3xl" />
        </div>

        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-3xl text-center">
            <div 
              className={`mb-6 inline-flex items-center gap-2 rounded-full border border-border bg-card px-4 py-1.5 text-sm text-muted-foreground transition-all duration-700 delay-100 ${
                showContent ? "opacity-100 translate-y-0" : "opacity-0 translate-y-5"
              }`}
            >
              <Sparkles className="h-4 w-4 text-accent" />
              <span>Your journey to better health starts here</span>
            </div>
            
            <h1 
              className={`text-balance text-4xl font-bold tracking-tight text-foreground transition-all duration-700 delay-200 sm:text-5xl md:text-6xl lg:text-7xl ${
                showContent ? "opacity-100 translate-y-0" : "opacity-0 translate-y-5"
              }`}
            >
              Nourish Your Body,{" "}
              <span className="text-secondary">Transform</span> Your Life
            </h1>
            
            <p 
              className={`mx-auto mt-6 max-w-2xl text-pretty text-lg text-muted-foreground transition-all duration-700 delay-300 sm:text-xl ${
                showContent ? "opacity-100 translate-y-0" : "opacity-0 translate-y-5"
              }`}
            >
              BiteRight helps you set fitness goals, track meals, monitor nutrition, and log workouts. 
              Get detailed daily summaries to maintain a balanced and healthy lifestyle.
            </p>

            <div 
              className={`mt-10 flex flex-col items-center justify-center gap-4 transition-all duration-700 delay-400 sm:flex-row ${
                showContent ? "opacity-100 translate-y-0" : "opacity-0 translate-y-5"
              }`}
            >
              <Button size="lg" asChild className="group bg-primary text-primary-foreground hover:bg-primary/90">
                <Link href="/register">
                  Start Free Today
                  <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link href="#features">See How It Works</Link>
              </Button>
            </div>

            <div 
              className={`mt-12 flex flex-wrap items-center justify-center gap-x-8 gap-y-4 text-sm text-muted-foreground transition-all duration-700 delay-500 ${
                showContent ? "opacity-100 translate-y-0" : "opacity-0 translate-y-5"
              }`}
            >
              <div className="flex items-center gap-2">
                <span className="flex h-2 w-2 rounded-full bg-secondary" />
                <span>Free to start</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="flex h-2 w-2 rounded-full bg-secondary" />
                <span>No credit card required</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="flex h-2 w-2 rounded-full bg-secondary" />
                <span>Cancel anytime</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}
