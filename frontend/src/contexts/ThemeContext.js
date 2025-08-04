import React, { createContext, useContext, useState, useEffect } from 'react';

/**
 * Theme Context for managing dark/light mode state across the application
 * 
 * Provides theme state and toggle functionality with localStorage persistence
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */

const ThemeContext = createContext();

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

export const ThemeProvider = ({ children }) => {
  // Initialize theme from localStorage or default to dark
  const [isDarkMode, setIsDarkMode] = useState(() => {
    const savedTheme = localStorage.getItem('aircraft-monitoring-theme');
    return savedTheme ? JSON.parse(savedTheme) : true; // Default to dark mode
  });

  // Update localStorage and document class when theme changes
  useEffect(() => {
    localStorage.setItem('aircraft-monitoring-theme', JSON.stringify(isDarkMode));
    
    // Add or remove dark class from document root
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDarkMode]);

  const toggleTheme = () => {
    setIsDarkMode(prev => !prev);
  };

  const value = {
    isDarkMode,
    toggleTheme,
    theme: isDarkMode ? 'dark' : 'light'
  };

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
};