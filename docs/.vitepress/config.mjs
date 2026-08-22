import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: 'Corde Bohème Docs',
  description: 'Documentation for the Corde Bohème microservices project',
  cleanUrls: true,
  lastUpdated: true,
  // Example localhost URLs in docs (e.g. http://localhost:8761) are literal
  // references and should not fail the build
  ignoreDeadLinks: [/^https?:\/\/(localhost|127\.0\.0\.1)/],
  // GitHub Pages project site is served under the repo name
  base: '/corde-boheme/',

  themeConfig: {
    siteTitle: 'Corde Bohème Docs',

    nav: [
      { text: 'Home', link: '/' },
      { text: 'Setup', link: '/setup/Setup' },
      { text: 'Docker & PostgreSQL', link: '/ReadMeFile/DockersetupForPostGres' },
      { text: 'Troubleshooting', link: '/troubleshouting/maven-errors' },
    ],

    sidebar: [
      {
        text: 'Project',
        collapsed: false,
        items: [
          { text: 'Parent Project', link: '/project/parent-project' },
          { text: 'Common Module', link: '/project/common-module' },
        ],
      },
      {
        text: 'Setup',
        collapsed: false,
        items: [
          { text: 'Project Setup', link: '/setup/Setup' },
          { text: 'Docker Setup for PostgreSQL', link: '/ReadMeFile/DockersetupForPostGres' },
        ],
      },
      {
        text: 'Infrastructure',
        collapsed: false,
        items: [
          { text: 'Config Server', link: '/infrastructure/config-server' },
          { text: 'Discovery Server', link: '/infrastructure/discovery-server' },
          { text: 'Gateway', link: '/infrastructure/gateway' },
        ],
      },
      {
        text: 'Interview Notes',
        collapsed: false,
        items: [
          { text: 'Config Server', link: '/interview/config-server' },
          { text: 'DTO', link: '/interview/dto' },
          { text: 'Eureka', link: '/interview/eureka' },
          { text: 'Gateway', link: '/interview/gateway' },
          { text: 'Service', link: '/interview/Service' },
        ],
      },
      {
        text: 'Troubleshooting',
        collapsed: false,
        items: [
          { text: 'Config Server Errors', link: '/troubleshouting/config-server-errors' },
          { text: 'Discovery Server Errors', link: '/troubleshouting/discovery-server-errors' },
          { text: 'Docker Errors', link: '/troubleshouting/docker-errors' },
          { text: 'Gateway Errors', link: '/troubleshouting/gateway-errors' },
          { text: 'JUnit Errors', link: '/troubleshouting/junit-errors' },
          { text: 'Maven Errors', link: '/troubleshouting/maven-errors' },
        ],
      },
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/sanadilnashin/corde-boheme' },
    ],

    footer: {
      message: 'Handcrafted macramé, woven with soul.',
      copyright: '© Corde Bohème',
    },

    outline: { level: [2, 3], label: 'On this page' },

    search: {
      provider: 'local',
      options: {
        translations: {
          button: { buttonText: 'Search docs', buttonAriaLabel: 'Search docs' },
          modal: {
            noResultsText: 'No results found',
            resetButtonTitle: 'Clear search',
            footer: { selectText: 'to select', navigateText: 'to navigate', closeText: 'to close' },
          },
        },
      },
    },

    docFooter: {
      prev: 'Previous',
      next: 'Next',
    },

    lastUpdated: {
      text: 'Last updated',
      formatOptions: { dateStyle: 'short', timeStyle: 'short' },
    },
  },
})